package io.github.codeyunze.service.impl;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.service.QofExtService;

/**
 * 无持久化时的默认扩展实现（不落库）。
 * <p>
 * 引入 {@code qof-persistence-mybatis} 后会被基于 DB 的实现覆盖。
 *
 * @author yunze
 * @since 17.2.0
 */
public class NoopQofExtService implements QofExtService {

    @Override
    public QofFileInfoBo getFileInfoByFileId(Long fileId) {
        throw new DataNotExistException("未启用元数据持久化，无法按 fileId 查询。请引入 qof-spring-boot-starter-persistence 或自行实现 QofExtService");
    }

    @Override
    public Long beforeUpload(QofFileInfoDto<?> fileDto) {
        return fileDto.getFileId();
    }

    @Override
    public QofFileInfoBo<?> afterUpload(QofFileInfoDto<?> fileDto) {
        QofFileInfoBo<Object> bo = new QofFileInfoBo<>();
        bo.setFileId(fileDto.getFileId());
        return bo;
    }

    @Override
    public void beforeDownload(QofFileInfoBo<?> fileBo) {
    }

    @Override
    public void afterDownload(QofFileInfoBo<?> fileBo) {
    }

    @Override
    public boolean beforeDelete(QofFileInfoBo<?> fileBo) {
        return true;
    }

    @Override
    public boolean afterDelete(QofFileInfoBo<?> fileBo, boolean deleted) {
        return true;
    }
}
