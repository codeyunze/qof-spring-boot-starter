package io.github.codeyunze.core;

import cn.hutool.core.util.IdUtil;
import io.github.codeyunze.config.QofConstant;
import io.github.codeyunze.config.QofProperties;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.validation.CoreFileValidationService;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.TypeNotSupportedException;
import io.github.codeyunze.utils.FileMetadataConverters;
import io.github.codeyunze.lifecycle.FileLifecycleListener;
import io.github.codeyunze.metadata.FileMetadataQuery;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.lifecycle.DeleteContext;
import io.github.codeyunze.lifecycle.DownloadContext;
import io.github.codeyunze.lifecycle.UploadContext;
import io.github.codeyunze.metadata.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ObjectProvider<FileMetadataQuery> metadataQueryProvider;

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

    /**
     * 批量获取文件预览地址。
     * <p>
     * 优先通过 {@link FileMetadataQuery#listByIds(List)} 一次查出元数据，未提供查询 SPI 时回落逐条 {@code findById}。
     * 返回列表与入参顺序、长度一致；文件不存在或未配置预览地址时对应元素为 {@code null}。
     */
    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, FileMetadata> metadataMap = loadMetadataByIds(fileIds);
        List<String> previewUrls = new ArrayList<>(fileIds.size());
        for (Long fileId : fileIds) {
            previewUrls.add(buildPreviewUrl(fileId == null ? null : metadataMap.get(fileId)));
        }
        return previewUrls;
    }

    /**
     * 批量加载元数据；有 {@link FileMetadataQuery} 时走 {@code listByIds}，否则逐条查询。
     */
    private Map<Long, FileMetadata> loadMetadataByIds(List<Long> fileIds) {
        FileMetadataQuery query = metadataQueryProvider != null ? metadataQueryProvider.getIfAvailable() : null;
        if (query != null) {
            List<FileMetadata> records = query.listByIds(fileIds);
            Map<Long, FileMetadata> metadataMap = new HashMap<>();
            if (records != null) {
                for (FileMetadata metadata : records) {
                    if (metadata != null && metadata.getFileId() != null) {
                        metadataMap.put(metadata.getFileId(), metadata);
                    }
                }
            }
            return metadataMap;
        }

        Map<Long, FileMetadata> metadataMap = new HashMap<>();
        for (Long fileId : fileIds) {
            if (fileId == null || metadataMap.containsKey(fileId)) {
                continue;
            }
            metadataRepository.findById(fileId).ifPresent(metadata -> metadataMap.put(fileId, metadata));
        }
        return metadataMap;
    }

    /**
     * 按配置的预览地址前缀拼接文件路径。
     * <p>
     * 元数据不存在、路径为空或未配置预览地址时返回 {@code null}。
     */
    private String buildPreviewUrl(FileMetadata metadata) {
        if (metadata == null || !StringUtils.hasText(metadata.getFilePath())) {
            return null;
        }
        QofFileInfoBo<?> fileBo = FileMetadataConverters.toBo(metadata);
        String previewAddress = resolvePreviewAddress(fileBo);
        if (!StringUtils.hasText(previewAddress)) {
            log.warn("未配置文件预览地址，无法生成预览链接, fileId={}", metadata.getFileId());
            return null;
        }
        return joinPreviewUrl(previewAddress, metadata.getFilePath());
    }

    /**
     * 解析预览地址前缀。默认使用 {@code qof.preview-address}，存储实现可覆盖为存储站配置。
     */
    protected String resolvePreviewAddress(QofFileInfoBo<?> fileBo) {
        return qofProperties != null ? qofProperties.getPreviewAddress() : null;
    }

    private String joinPreviewUrl(String previewAddress, String filePath) {
        String prefix = previewAddress.endsWith("/")
                ? previewAddress.substring(0, previewAddress.length() - 1)
                : previewAddress;
        String path = filePath.startsWith("/") ? filePath : "/" + filePath;
        return prefix + path;
    }

    private QofFileInfoBo<?> requireFileBo(Long fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new DataNotExistException("文件信息不存在"));
        return FileMetadataConverters.toBo(metadata);
    }

    /**
     * 将文件写入具体对象存储。
     * <p>
     * 模板方法 {@link #upload} 已完成校验、路径生成与生命周期回调，实现类只需负责落盘 / 上传。
     *
     * @param fis  已完成前置校验的文件流
     * @param info 上传文件信息（含 fileId、filePath、存储站等）
     * @return 文件唯一 id
     */
    protected abstract Long doUpload(InputStream fis, QofFileInfoDto<?> info);

    /**
     * 从具体对象存储读取文件流。
     * <p>
     * {@link #download} 与 {@link #preview} 共用本方法；调用方负责关闭返回的输入流。
     *
     * @param fileBo 已从元数据加载的文件信息
     * @return 下载结果（含输入流、文件名、大小、类型）
     */
    protected abstract QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo);

    /**
     * 从具体对象存储删除文件对象。
     * <p>
     * 模板方法 {@link #delete} 负责元数据与生命周期；对象删除失败时由模板方法记录日志，实现类无需补偿元数据。
     *
     * @param fileBo 已从元数据加载的文件信息
     * @return {@code true} 对象删除成功；{@code false} 对象删除失败
     */
    protected abstract boolean doDelete(QofFileInfoBo<?> fileBo);
}
