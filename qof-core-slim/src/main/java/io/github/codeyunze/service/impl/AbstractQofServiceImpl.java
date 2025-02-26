package io.github.codeyunze.service.impl;

import io.github.codeyunze.QofProperties;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.service.SysFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * QOF文件信息操作扩展接口默认实现
 *
 * @author yunze
 * @since 2025/2/18 07:49
 */
@Service
public abstract class AbstractQofServiceImpl implements QofExtService {

    private static final Logger log = LoggerFactory.getLogger(AbstractQofServiceImpl.class);

    private final SysFilesService filesService;

    public AbstractQofServiceImpl(SysFilesService filesService) {
        this.filesService = filesService;
    }

    @Resource
    private QofProperties qofProperties;

    @Override
    public QofFileInfoBo<?> getFileInfoByFileId(Long fileId) {
        QofFileInfoBo<?> fileBo = filesService.getByFileId(fileId);
        if (fileBo == null) {
            throw new DataNotExistException("文件信息不存在");
        }
        return fileBo;
    }

    @Override
    public Long beforeUpload(QofFileInfoDto<?> fileDto) {
        log.info("扩展-文件上传前执行");
        return fileDto.getFileId();
    }

    @Override
    public QofFileInfoBo<?> afterUpload(QofFileInfoDto<?> fileDto) {
        log.info("扩展-文件上传后执行");
        if (qofProperties.isPersistentEnable()) {
            return filesService.save(fileDto);
        }
        QofFileInfoBo<?> fileBo = new QofFileInfoBo<>();
        BeanUtils.copyProperties(fileDto, fileBo);
        return fileBo;
    }

    @Override
    public void beforeDownload(QofFileInfoBo<?> fileBo) {
        log.info("扩展-文件下载前执行");
    }

    @Override
    public void afterDownload(QofFileInfoBo<?> fileBo) {
        log.info("扩展-文件下载后执行");
    }

    @Override
    public boolean beforeDelete(QofFileInfoBo<?> fileBo) {
        log.info("扩展-文件删除前执行");
        return filesService.deleteByFileId(fileBo.getFileId());
    }

    @Override
    public boolean afterDelete(QofFileInfoBo<?> fileBo) {
        log.info("扩展-文件删除后执行");
        return true;
    }
}
