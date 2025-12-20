package io.github.codeyunze.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.bo.SysFilesMetaBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.entity.SysFiles;

/**
 * 系统-文件表(SysFiles)表服务接口
 *
 * @author 高晗
 * @since 2025-02-16 15:43:41
 */
public interface SysFilesService extends IService<SysFiles> {

    /**
     * 新增数据
     *
     * @param fileDto 新增基础数据
     * @return 主键 Id
     */
    QofFileInfoBo<?> save(QofFileInfoDto<?> fileDto);

    /**
     * 根据文件 Id 获取文件信息
     *
     * @param fileId 文件 Id
     * @return 文件基础信息
     */
    QofFileInfoBo<?> getByFileId(Long fileId);

    /**
     * 根据文件 Id 删除文件信息
     *
     * @param fileId 文件 Id
     * @return true：删除成功; false: 删除失败;
     */
    boolean deleteByFileId(Long fileId);

    /**
     * 分页查询文件列表（仅元数据）
     *
     * @param page               分页
     * @param fileName           文件名（模糊匹配，可选）
     * @param fileStorageMode    存储模式（local/cos/oss，可选）
     * @param fileStorageStation 存储站（可选）
     * @return 分页结果
     */
    IPage<SysFilesMetaBo> pageFiles(Page<SysFiles> page,
                                    String fileName,
                                    String fileStorageMode,
                                    String fileStorageStation);

    /**
     * 校验文件访问权限
     *
     * @param fileId   文件 ID
     * @param createId 创建者ID（可选，当文件不公开时必须提供）
     * @throws io.github.codeyunze.exception.FileAccessDeniedException 如果访问被拒绝
     */
    void checkFileAccessPermission(Long fileId, Long createId);

}

