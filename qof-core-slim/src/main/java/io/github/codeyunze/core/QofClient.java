package io.github.codeyunze.core;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.dto.QofFileInfoDto;

import java.io.InputStream;

/**
 * QOF客户端操作接口
 *
 * @author 高晗
 * @since 2025/2/17 08:46
 */
public interface QofClient {

    /**
     * 上传文件信息
     *
     * @param fis  上传文件的输入流
     * @param info 上传文件的基础信息
     * @return 文件唯一id
     */
    Long upload(InputStream fis, QofFileInfoDto<?> info);

    /**
     * 下载文件
     *
     * @param fileId 文件唯一id
     * @return 文件流数据
     */
    QofFileDownloadBo download(Long fileId);

    /**
     * 预览文件
     *
     * @param fileId 文件唯一id
     * @return 文件流数据
     */
    QofFileDownloadBo preview(Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 删除文件的唯一id
     * @return true: 删除成功；  false: 删除失败；
     */
    boolean delete(Long fileId);
}
