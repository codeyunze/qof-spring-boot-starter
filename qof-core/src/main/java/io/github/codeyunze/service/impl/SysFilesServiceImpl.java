package io.github.codeyunze.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.codeyunze.bo.SysFilesMetaBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.exception.FileAccessDeniedException;
import io.github.codeyunze.mapper.SysFilesMapper;
import io.github.codeyunze.service.SysFilesService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 系统-文件表(SysFiles)表服务实现类
 *
 * @author 高晗
 * @since 2025-02-16 15:43:41
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements SysFilesService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QofFileInfoBo<?> save(QofFileInfoDto<?> fileDto) {
        SysFiles fileDo = new SysFiles();
        BeanUtils.copyProperties(fileDto, fileDo);
        if (null == fileDto.getFileId()) {
            fileDo.setId(IdUtil.getSnowflakeNextId());
        } else {
            fileDo.setId(fileDto.getFileId());
        }
        baseMapper.insert(fileDo);
        QofFileInfoBo<?> fileBo = new QofFileInfoBo<>();
        BeanUtils.copyProperties(fileDo, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo<?> getByFileId(Long fileId) {
        return baseMapper.selectByFileId(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByFileId(Long fileId) {
        return baseMapper.deleteById(fileId) > 0;
    }

    @Override
    public IPage<SysFilesMetaBo> pageFiles(Page<SysFiles> page,
                                          String fileName,
                                          String fileStorageMode,
                                          String fileStorageStation) {
        // 参数兜底（属于服务层的入参保护）
        if (page == null) {
            page = new Page<>(1, 10);
        }
        if (page.getCurrent() < 1) {
            page.setCurrent(1);
        }
        if (page.getSize() < 1) {
            page.setSize(10);
        }

        LambdaQueryWrapper<SysFiles> wrapper = new LambdaQueryWrapper<>();
        // 仅查询有效数据（invalid=0 表示有效）
        wrapper.eq(SysFiles::getInvalid, 0L);
        wrapper.orderByDesc(SysFiles::getCreateTime);

        // 只查询列表需要的字段，避免返回/暴露 filePath
        wrapper.select(
                SysFiles::getId,
                SysFiles::getCreateTime,
                SysFiles::getUpdateTime,
                SysFiles::getFileName,
                SysFiles::getFileType,
                SysFiles::getFileLabel,
                SysFiles::getFileSize,
                SysFiles::getFileStorageMode,
                SysFiles::getFileStorageStation
        );

        if (StringUtils.hasText(fileName)) {
            wrapper.like(SysFiles::getFileName, fileName.trim());
        }
        if (StringUtils.hasText(fileStorageMode)) {
            wrapper.eq(SysFiles::getFileStorageMode, fileStorageMode.trim().toLowerCase());
        }
        if (StringUtils.hasText(fileStorageStation)) {
            wrapper.eq(SysFiles::getFileStorageStation, fileStorageStation.trim());
        }

        IPage<SysFiles> entityPage = this.page(page, wrapper);
        Page<SysFilesMetaBo> metaPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        metaPage.setPages(entityPage.getPages());
        metaPage.setRecords(entityPage.getRecords().stream().map(this::toMetaBo).collect(java.util.stream.Collectors.toList()));
        return metaPage;
    }

    @Override
    public void checkFileAccessPermission(Long fileId, Long createId) {
        QofFileInfoBo<?> fileBo = getByFileId(fileId);
        
        // 如果文件是公开的（publicAccess == 1），允许访问
        if (fileBo.getPublicAccess() != null && fileBo.getPublicAccess() == 1) {
            return;
        }
        
        // 如果文件不公开（publicAccess == 0 或 null），需要校验 createId
        if (createId == null) {
            throw new FileAccessDeniedException("文件访问被拒绝：该文件为私有文件，需要提供创建者ID");
        }
        
        // 校验 createId 是否匹配
        if (fileBo.getCreateId() == null || !fileBo.getCreateId().equals(createId)) {
            throw new FileAccessDeniedException("文件访问被拒绝：创建者ID不匹配");
        }
    }

    private SysFilesMetaBo toMetaBo(SysFiles entity) {
        SysFilesMetaBo bo = new SysFilesMetaBo();
        if (entity == null) {
            return bo;
        }
        bo.setId(entity.getId());
        bo.setCreateTime(entity.getCreateTime());
        bo.setUpdateTime(entity.getUpdateTime());
        bo.setFileName(entity.getFileName());
        bo.setFileType(entity.getFileType());
        bo.setFileLabel(entity.getFileLabel());
        bo.setFileSize(entity.getFileSize());
        bo.setFileStorageMode(entity.getFileStorageMode());
        bo.setFileStorageStation(entity.getFileStorageStation());
        return bo;
    }
}

