package io.github.codeyunze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.entity.SysFiles;

/**
 * 系统-文件表(SysFiles)表服务接口
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
public interface SysFilesService extends IService<SysFiles> {

    /**
     * 新增数据
     *
     * @param fileDto 新增基础数据
     * @return 主键Id
     */
    QofFileInfoBo<?> save(QofFileInfoDto<?> fileDto);

    /**
     * 根据文件Id获取文件信息
     *
     * @param fileId 文件Id
     * @return 文件基础信息
     */
    QofFileInfoBo<?> getByFileId(Long fileId);

    /**
     * 根据文件Id删除文件信息
     *
     * @param fileId 文件Id
     * @return true：删除成功; false: 删除失败;
     */
    boolean deleteByFileId(Long fileId);

}

