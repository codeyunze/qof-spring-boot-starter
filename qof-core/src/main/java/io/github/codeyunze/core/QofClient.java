package io.github.codeyunze.core;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.dto.QofFileInfoDto;

import java.io.InputStream;
import java.util.List;

/**
 * QOF 客户端操作接口
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
     * @return 文件唯一 id
     */
    Long upload(InputStream fis, QofFileInfoDto<?> info);

    /**
     * 下载文件
     *
     * @param fileId 文件唯一 id
     * @return 文件流数据
     * @apiNote 返回的 QofFileDownloadBo 中的InputStream需要调用者负责关闭
     */
    QofFileDownloadBo download(Long fileId);

    /**
     * 预览文件
     *
     * @param fileId 文件唯一 id
     * @return 文件流数据
     * @apiNote 返回的 QofFileDownloadBo 中的InputStream需要调用者负责关闭
     */
    QofFileDownloadBo preview(Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 删除文件的唯一 id
     * @return true: 删除成功；  false: 删除失败；
     */
    boolean delete(Long fileId);

    /**
     * 批量获取文件预览地址。
     * <p>
     * 由 {@code qof.preview-address} 或各存储站 {@code preview-address} 与文件路径拼接而成。
     * 返回列表与入参顺序、长度一致；文件不存在或未配置预览地址时对应元素为 {@code null}。
     *
     * @param fileIds 文件 ID
     * @return 文件预览地址
     */
    List<String> getFilePreviewByFileIds(List<Long> fileIds);
}
