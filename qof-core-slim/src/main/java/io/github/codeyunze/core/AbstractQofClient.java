package io.github.codeyunze.core;

import cn.hutool.core.util.IdUtil;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.TypeNotSupportedException;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QOF 客户端操作抽象接口
 *
 * @author 高晗
 * @since 2025/2/20 15:52
 */
public abstract class AbstractQofClient implements QofClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractQofClient.class);

    private final QofExtService qofExtService;

    public AbstractQofClient(QofExtService qofExtService) {
        this.qofExtService = qofExtService;
    }


    /**
     * 上传文件信息
     *
     * @param fis  上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一id
     */
    @Override
    public Long upload(InputStream fis, QofFileInfoDto<?> info) {
        log.info("通用的上传前处理逻辑");
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
        Long fileId = doUpload(fis, info);
        // 扩展-文件上传后操作
        qofExtService.afterUpload(info);
        return fileId;
    }

    /**
     * 下载文件
     *
     * @param fileId 文件唯一id
     * @return 文件流数据
     */
    @Override
    public QofFileDownloadBo download(Long fileId) {
        log.info("通用的下载处理逻辑");
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
        log.info("通用的文件预览处理逻辑");
        // 查询文件相关信息
        QofFileInfoBo<?> fileBo = qofExtService.getFileInfoByFileId(fileId);
        List<String> supportedTypes = new ArrayList<>(Arrays.asList("image/png", "image/jpeg", "application/pdf"));
        if (!supportedTypes.contains(fileBo.getFileType().toLowerCase())) {
            throw new TypeNotSupportedException("暂不支持[" + fileBo.getFileType() + "]文件的预览");
        }
        // 执行具体的文件预览操作
        return doDownload(fileBo);
    }

    /**
     * 删除文件
     *
     * @param fileId 删除文件的唯一id
     */
    @Override
    public boolean delete(Long fileId) {
        log.info("通用的删除前处理逻辑");
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
     * @return 文件唯一id
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
     * @param fileBo 删除文件的唯一id
     */
    protected abstract boolean doDelete(QofFileInfoBo<?> fileBo);
}
