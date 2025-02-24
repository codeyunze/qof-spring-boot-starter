package io.github.codeyunze.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.mapper.SysFilesMapper;
import io.github.codeyunze.service.SysFilesService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-文件表(SysFiles)表服务实现类
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements SysFilesService {

    @Override
    public QofFileInfoBo save(QofFileInfoDto fileDto) {
        SysFiles fileDo = new SysFiles();
        BeanUtils.copyProperties(fileDto, fileDo);
        if (null == fileDto.getFileId()) {
            fileDo.setId(IdUtil.getSnowflakeNextId());
        } else {
            fileDo.setId(fileDto.getFileId());
        }
        baseMapper.insert(fileDo);
        QofFileInfoBo fileBo = new QofFileInfoBo();
        BeanUtils.copyProperties(fileDo, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo getByFileId(Long fileId) {
        return baseMapper.selectByFileId(fileId);
    }

    @Override
    public boolean deleteByFileId(Long fileId) {
        return baseMapper.deleteById(fileId) > 0;
    }
}

