package io.github.codeyunze.persistence.mysql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.codeyunze.persistence.mysql.internal.SysFilesEntity;
import io.github.codeyunze.persistence.mysql.internal.SysFilesMapper;
import io.github.codeyunze.metadata.FileMetadataQuery;
import io.github.codeyunze.metadata.FileMetadata;
import io.github.codeyunze.metadata.FileMetadataQueryCriteria;
import io.github.codeyunze.metadata.PageResult;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MySQL 元数据分页查询。
 */
public class MysqlFileMetadataQuery implements FileMetadataQuery {

    private final SysFilesMapper mapper;

    public MysqlFileMetadataQuery(SysFilesMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PageResult<FileMetadata> page(FileMetadataQueryCriteria criteria) {
        long pageNum = criteria.getPageNum() < 1 ? 1 : criteria.getPageNum();
        long pageSize = criteria.getPageSize() < 1 ? 10 : criteria.getPageSize();

        LambdaQueryWrapper<SysFilesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysFilesEntity::getCreateTime);
        wrapper.select(
                SysFilesEntity::getId,
                SysFilesEntity::getCreateTime,
                SysFilesEntity::getUpdateTime,
                SysFilesEntity::getFileName,
                SysFilesEntity::getFileType,
                SysFilesEntity::getFileLabel,
                SysFilesEntity::getFileSize,
                SysFilesEntity::getFileStorageMode,
                SysFilesEntity::getFileStorageStation,
                SysFilesEntity::getPublicAccess,
                SysFilesEntity::getCreateId
        );

        // 条件参数会立即求值，空条件不能写在 like/eq 的第三个参数里
        if (StringUtils.hasText(criteria.getFileName())) {
            wrapper.like(SysFilesEntity::getFileName, criteria.getFileName().trim());
        }
        if (StringUtils.hasText(criteria.getFileStorageMode())) {
            wrapper.eq(SysFilesEntity::getFileStorageMode, criteria.getFileStorageMode().trim().toLowerCase());
        }
        if (StringUtils.hasText(criteria.getFileStorageStation())) {
            wrapper.eq(SysFilesEntity::getFileStorageStation, criteria.getFileStorageStation().trim());
        }

        IPage<SysFilesEntity> entityPage = mapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return new PageResult<>(
                entityPage.getRecords().stream().map(MysqlFileMetadataRepository::toMetadata).collect(Collectors.toList()),
                entityPage.getTotal(),
                entityPage.getCurrent(),
                entityPage.getSize()
        );
    }

    @Override
    public List<FileMetadata> listByIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = fileIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysFilesEntity> entities = mapper.selectByIds(ids);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(MysqlFileMetadataRepository::toMetadata).collect(Collectors.toList());
    }
}
