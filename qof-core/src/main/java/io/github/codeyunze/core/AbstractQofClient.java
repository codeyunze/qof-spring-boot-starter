package io.github.codeyunze.core;

import cn.hutool.core.util.IdUtil;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.validation.CoreFileValidationService;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.FileAccessDeniedException;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.TypeNotSupportedException;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * QOF 客户端操作抽象接口
 *
 * @author 高晗
 * @since 2025/2/20 15:52
 */
public abstract class AbstractQofClient implements QofClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractQofClient.class);

    private final QofExtService qofExtService;

    @Resource
    private QofProperties qofProperties;

    @Resource
    private CoreFileValidationService coreFileValidationService;

    public AbstractQofClient(QofExtService qofExtService) {
        this.qofExtService = qofExtService;
    }


    /**
     * 上传文件信息
     *
     * @param fis  上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一 id
     */
    @Override
    public Long upload(InputStream fis, QofFileInfoDto<?> info) {
        log.debug("通用的上传前处理逻辑");

        if (Objects.equals(info.getPublicAccess(), QofConstant.PRIVATE_ACCESS) && info.getCreateId() == null) {
            throw new FileUploadException("私有文件必须指定文件所有者");
        }
        
        // 为了支持Magic Number检测，需要将流包装为BufferedInputStream以支持mark/reset
        // 这样无论从web还是第三方系统调用，都能执行完整的校验逻辑
        InputStream validationStream = fis;
        if (fis != null && !fis.markSupported()) {
            validationStream = new BufferedInputStream(fis, 8192);
            // 标记位置，以便Magic Number检测后可以重置
            validationStream.mark(8192);
        }
        
        // 核心校验：无论从web还是第三方系统调用，都会执行相同的校验逻辑
        // 包括：文件名安全性、文件大小、文件类型（Magic Number检测）
        coreFileValidationService.validateBeforeUpload(validationStream, info);
        
        // 如果流被包装了，重置到开始位置以便后续上传使用
        if (validationStream != fis && validationStream.markSupported()) {
            try {
                validationStream.reset();
            } catch (Exception e) {
                log.warn("重置流失败，将使用原始流: {}", e.getMessage());
                validationStream = fis;
            }
        }
        
        // 使用重置后的流进行上传（如果重置成功）或原始流
        InputStream uploadStream = (validationStream != fis && validationStream.markSupported()) 
                ? validationStream : fis;
        
        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }
        
        // 安全地提取文件后缀，防止文件名没有点号的情况
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

        // 扩展-文件上传前操作
        qofExtService.beforeUpload(info);
        // 执行具体的文件上传操作
        Long fileId = doUpload(uploadStream, info);
        // 扩展-文件上传后操作
        qofExtService.afterUpload(info);
        return fileId;
    }

    /**
     * 下载文件
     *
     * @param fileId 文件唯一 id
     * @return 文件流数据
     */
    @Override
    public QofFileDownloadBo download(Long fileId) {
        log.debug("通用的下载处理逻辑");
        // 查询文件相关信息
        QofFileInfoBo<?> fileBo = qofExtService.getFileInfoByFileId(fileId);
        // 扩展-文件下载前操作
        qofExtService.beforeDownload(fileBo);
        // 执行具体的文件下载操作
        QofFileDownloadBo fileDownloadBo = doDownload(fileBo);
        // 扩展-文件下载后操作
        qofExtService.afterDownload(fileBo);
        return fileDownloadBo;
    }

    @Override
    public QofFileDownloadBo preview(Long fileId) {
        log.debug("通用的文件预览处理逻辑");
        // 查询文件相关信息
        QofFileInfoBo<?> fileBo = qofExtService.getFileInfoByFileId(fileId);
        
        // 检查文件类型是否为空
        if (fileBo.getFileType() == null || fileBo.getFileType().trim().isEmpty()) {
            throw new TypeNotSupportedException("文件类型为空，无法预览");
        }
        
        // 使用配置的预览支持类型列表
        List<String> supportedTypes = qofProperties != null && qofProperties.getPreviewSupportedTypes() != null
                ? qofProperties.getPreviewSupportedTypes()
                : new ArrayList<>(Arrays.asList("image/png", "image/jpeg", "application/pdf"));
        
        String fileType = fileBo.getFileType().toLowerCase();
        if (!supportedTypes.contains(fileType)) {
            throw new TypeNotSupportedException("暂不支持[" + fileBo.getFileType() + "]文件的预览");
        }
        // 执行具体的文件预览操作
        return doDownload(fileBo);
    }

    /**
     * 删除文件
     *
     * @param fileId 删除文件的唯一 id
     */
    @Override
    public boolean delete(Long fileId) {
        log.debug("通用的删除前处理逻辑");
        QofFileInfoBo<?> fileBo = qofExtService.getFileInfoByFileId(fileId);
        if (fileBo == null) {
            return true;
        }
        // 有文件信息，但是没有删除成功
        if (!qofExtService.beforeDelete(fileBo)) {
            return false;
        }

        boolean delete = doDelete(fileBo);

        qofExtService.afterDelete(fileBo, delete);

        return delete;
    }


    /**
     * 具体执行-上传文件信息
     *
     * @param fis  上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一 id
     */
    protected abstract Long doUpload(InputStream fis, QofFileInfoDto<?> info);

    /**
     * 具体执行-下载文件
     *
     * @param fileBo 文件信息
     * @return 文件流数据
     */
    protected abstract QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo);

    /**
     * 具体执行-删除文件
     *
     * @param fileBo 删除文件的唯一 id
     */
    protected abstract boolean doDelete(QofFileInfoBo<?> fileBo);
}
