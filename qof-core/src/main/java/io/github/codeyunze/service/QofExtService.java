package io.github.codeyunze.service;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;

/**
 * QOF文件信息操作扩展接口
 *
 * @author 高晗
 * @since 2025/2/18 07:47
 */
public interface QofExtService {

    /**
     * 根据文件 Id 查询文件信息
     *
     * @param fileId 文件 Id
     * @return QofFileInfoBo 文件信息
     */
    QofFileInfoBo getFileInfoByFileId(Long fileId);

    /**
     * 文件上传之前
     *
     * @param fileDto 新增文件基础数据
     * @return 主键Id, 如果上传失败则返回null
     */
    Long beforeUpload(QofFileInfoDto<?> fileDto);

    /**
     * 文件上传之后
     *
     * @param fileDto 文件基础数据
     * @return 主键Id, 如果上传失败则返回null
     */
    QofFileInfoBo<?> afterUpload(QofFileInfoDto<?> fileDto);

    /**
     * 下载前执行操作
     *
     * @param fileBo 文件信息
     */
    void beforeDownload(QofFileInfoBo<?> fileBo);

    /**
     * 下载后执行操作
     *
     * @param fileBo 文件信息
     */
    void afterDownload(QofFileInfoBo<?> fileBo);

    /**
     * 文件删除前执行操作
     *
     * @param fileBo 删除文件信息
     * @return true: 文件删除前执行操作成功；   false: 文件删除前执行操作失败；
     */
    boolean beforeDelete(QofFileInfoBo<?> fileBo);

    /**
     * 文件删除后执行操作
     *
     * @param fileBo  删除文件信息
     * @param deleted 删除结果 true: 文件删除成功 false: 文件删除失败
     * @return true: 文件删除后执行操作成功；   false: 文件删除后执行操作失败；
     */
    boolean afterDelete(QofFileInfoBo<?> fileBo, boolean deleted);
}
