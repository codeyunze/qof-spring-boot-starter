package io.github.codeyunze.core;

import cn.hutool.core.util.IdUtil;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.validation.CoreFileValidationService;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.TypeNotSupportedException;
import io.github.codeyunze.metadata.FileMetadataConverters;
import io.github.codeyunze.spi.FileLifecycleListener;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.lifecycle.DeleteContext;
import io.github.codeyunze.spi.lifecycle.DownloadContext;
import io.github.codeyunze.spi.lifecycle.UploadContext;
import io.github.codeyunze.spi.metadata.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * QOF 客户端操作抽象接口。
 *
 * @author 高晗
 * @since 2025/2/20 15:52
 */
public abstract class AbstractQofClient implements QofClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractQofClient.class);

    private final FileMetadataRepository metadataRepository;
    private final List<FileLifecycleListener> lifecycleListeners;

    @Resource
    private QofProperties qofProperties;

    @Resource
    private CoreFileValidationService coreFileValidationService;

    public AbstractQofClient(FileMetadataRepository metadataRepository,
                             List<FileLifecycleListener> lifecycleListeners) {
        this.metadataRepository = Objects.requireNonNull(metadataRepository, "FileMetadataRepository 不能为空");
        this.lifecycleListeners = lifecycleListeners != null ? lifecycleListeners : Collections.emptyList();
    }

    @Override
    public Long upload(InputStream fis, QofFileInfoDto<?> info) {
        log.debug("通用的上传前处理逻辑");

        if (Objects.equals(info.getPublicAccess(), QofConstant.PRIVATE_ACCESS) && info.getCreateId() == null) {
            throw new FileUploadException("私有文件必须指定文件所有者");
        }

        InputStream validationStream = fis;
        if (fis != null && !fis.markSupported()) {
            validationStream = new BufferedInputStream(fis, 8192);
            validationStream.mark(8192);
        }

        coreFileValidationService.validateBeforeUpload(validationStream, info);

        if (validationStream != fis && validationStream.markSupported()) {
            try {
                validationStream.reset();
            } catch (Exception e) {
                log.warn("重置流失败，将使用原始流: {}", e.getMessage());
                validationStream = fis;
            }
        }

        InputStream uploadStream = (validationStream != fis && validationStream.markSupported())
                ? validationStream : fis;

        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }

        String suffix = "";
        String fileName = info.getFileName();
        if (fileName != null && fileName.contains(".")) {
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1) {
                suffix = fileName.substring(lastDotIndex).toLowerCase();
            }
        }

        String key = info.getDirectoryAddress() + "/" + info.getFileId() + suffix;
        info.setFilePath(key);

        UploadContext uploadContext = new UploadContext(FileMetadataConverters.fromDto(info));
        for (FileLifecycleListener listener : lifecycleListeners) {
            listener.beforeUpload(uploadContext);
        }

        Long fileId = doUpload(uploadStream, info);

        uploadContext.setMetadata(FileMetadataConverters.fromDto(info));
        for (FileLifecycleListener listener : lifecycleListeners) {
            listener.afterUpload(uploadContext);
        }
        return fileId;
    }

    @Override
    public QofFileDownloadBo download(Long fileId) {
        log.debug("通用的下载处理逻辑");
        QofFileInfoBo<?> fileBo = requireFileBo(fileId);
        DownloadContext downloadContext = new DownloadContext(FileMetadataConverters.fromBo(fileBo));
        for (FileLifecycleListener listener : lifecycleListeners) {
            listener.beforeDownload(downloadContext);
        }
        QofFileDownloadBo fileDownloadBo = doDownload(fileBo);
        for (FileLifecycleListener listener : lifecycleListeners) {
            listener.afterDownload(downloadContext);
        }
        return fileDownloadBo;
    }

    @Override
    public QofFileDownloadBo preview(Long fileId) {
        log.debug("通用的文件预览处理逻辑");
        QofFileInfoBo<?> fileBo = requireFileBo(fileId);

        if (fileBo.getFileType() == null || fileBo.getFileType().trim().isEmpty()) {
            throw new TypeNotSupportedException("文件类型为空，无法预览");
        }

        List<String> supportedTypes = qofProperties != null && qofProperties.getPreviewSupportedTypes() != null
                ? qofProperties.getPreviewSupportedTypes()
                : new ArrayList<>(Arrays.asList("image/png", "image/jpeg", "application/pdf"));

        String fileType = fileBo.getFileType().toLowerCase();
        if (!supportedTypes.contains(fileType)) {
            throw new TypeNotSupportedException("暂不支持[" + fileBo.getFileType() + "]文件的预览");
        }
        return doDownload(fileBo);
    }

    @Override
    public boolean delete(Long fileId) {
        log.debug("通用的删除前处理逻辑");
        FileMetadata metadata = metadataRepository.findById(fileId).orElse(null);
        if (metadata == null) {
            return true;
        }

        DeleteContext deleteContext = new DeleteContext(metadata);
        for (FileLifecycleListener listener : lifecycleListeners) {
            if (!listener.beforeDelete(deleteContext)) {
                return false;
            }
        }

        QofFileInfoBo<?> fileBo = FileMetadataConverters.toBo(metadata);
        boolean objectDeleted = false;
        try {
            objectDeleted = doDelete(fileBo);
            if (!objectDeleted) {
                log.warn("对象存储删除失败（元数据已删，暂不补偿）, fileId={}, path={}",
                        fileId, metadata.getFilePath());
            }
        } catch (Exception e) {
            log.warn("对象存储删除异常（元数据已删，暂不补偿）, fileId={}, path={}",
                    fileId, metadata.getFilePath(), e);
        }

        for (FileLifecycleListener listener : lifecycleListeners) {
            listener.afterDelete(deleteContext, objectDeleted);
        }
        return true;
    }

    private QofFileInfoBo<?> requireFileBo(Long fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new DataNotExistException("文件信息不存在"));
        return FileMetadataConverters.toBo(metadata);
    }

    protected abstract Long doUpload(InputStream fis, QofFileInfoDto<?> info);

    protected abstract QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo);

    protected abstract boolean doDelete(QofFileInfoBo<?> fileBo);
}
