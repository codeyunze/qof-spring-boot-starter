package io.github.codeyunze.persistence.mysql;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.codeyunze.persistence.mysql.internal.SysFilesEntity;
import io.github.codeyunze.persistence.mysql.internal.SysFilesMapper;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.metadata.FileMetadata;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * MySQL 元数据仓储实现。
 */
public class MysqlFileMetadataRepository extends ServiceImpl<SysFilesMapper, SysFilesEntity>
        implements FileMetadataRepository {

    public MysqlFileMetadataRepository(SysFilesMapper mapper) {
        this.baseMapper = mapper;
    }

    @Override
    public String type() {
        return "mysql";
    }

    @Override
    public Optional<FileMetadata> findById(Long fileId) {
        if (fileId == null) {
            return Optional.empty();
        }
        SysFilesEntity entity = getById(fileId);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toMetadata(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(FileMetadata metadata) {
        SysFilesEntity entity = new SysFilesEntity();
        BeanUtils.copyProperties(metadata, entity);
        entity.setId(metadata.getFileId());
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FileMetadata metadata) {
        SysFilesEntity entity = new SysFilesEntity();
        BeanUtils.copyProperties(metadata, entity);
        entity.setId(metadata.getFileId());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long fileId) {
        return removeById(fileId);
    }

    static FileMetadata toMetadata(SysFilesEntity entity) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(entity.getId());
        metadata.setFileName(entity.getFileName());
        metadata.setFilePath(entity.getFilePath());
        metadata.setFileType(entity.getFileType());
        metadata.setFileLabel(entity.getFileLabel());
        metadata.setFileSize(entity.getFileSize());
        metadata.setFileStorageMode(entity.getFileStorageMode());
        metadata.setFileStorageStation(entity.getFileStorageStation());
        metadata.setPublicAccess(entity.getPublicAccess());
        metadata.setCreateId(entity.getCreateId());
        metadata.setCreateTime(entity.getCreateTime());
        metadata.setUpdateTime(entity.getUpdateTime());
        return metadata;
    }
}
