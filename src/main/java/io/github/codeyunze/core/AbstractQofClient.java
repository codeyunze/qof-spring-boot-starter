package io.github.codeyunze.core;

import cn.hutool.core.util.IdUtil;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * QOF客户端操作抽象接口
 * @author yunze
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
    public Long upload(InputStream fis, QofFileInfoDto info) {
        log.info("通用的上传前处理逻辑");
        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }
        String suffix = info.getFileName().substring(info.getFileName().lastIndexOf(".")).toLowerCase();
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
        log.info("通用的下载前处理逻辑");
        qofExtService.beforeDownload(fileId);
        // 扩展-文件下载前操作
        QofFileInfoBo fileBo = qofExtService.getFileInfoByFileId(fileId);
        // 执行具体的文件下载操作
        QofFileDownloadBo fileDownloadBo = doDownload(fileBo);
        // 扩展-文件下载后操作
        qofExtService.afterDownload(fileId);
        return fileDownloadBo;
    }

    /**
     * 删除文件
     *
     * @param fileId 删除文件的唯一id
     */
    @Override
    public boolean delete(Long fileId) {
        log.info("通用的删除前处理逻辑");
        QofFileInfoBo fileBo = qofExtService.getFileInfoByFileId(fileId);
        if (fileBo == null) {
            return true;
        }
        // 有文件信息，但是没有删除成功
        if (!qofExtService.beforeDelete(fileId)) {
            return false;
        }

        return doDelete(fileBo);
    }


    /**
     * 具体执行-上传文件信息
     *
     * @param fis  上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一id
     */
    protected abstract Long doUpload(InputStream fis, QofFileInfoDto info);

    /**
     * 具体执行-下载文件
     *
     * @param fileBo 文件信息
     * @return 文件流数据
     */
    protected abstract QofFileDownloadBo doDownload(QofFileInfoBo fileBo);

    /**
     * 具体执行-删除文件
     *
     * @param fileBo 删除文件的唯一id
     */
    protected abstract boolean doDelete(QofFileInfoBo fileBo);
}
