package io.github.codeyunze.persistence.mongo;

import io.github.codeyunze.persistence.mongo.internal.SysFilesDocument;
import io.github.codeyunze.metadata.FileMetadataQuery;
import io.github.codeyunze.metadata.FileMetadata;
import io.github.codeyunze.metadata.FileMetadataQueryCriteria;
import io.github.codeyunze.metadata.PageResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MongoDB 元数据分页查询。
 */
public class MongoFileMetadataQuery implements FileMetadataQuery {

    private final MongoTemplate mongoTemplate;

    public MongoFileMetadataQuery(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PageResult<FileMetadata> page(FileMetadataQueryCriteria criteria) {
        long pageNum = criteria.getPageNum() < 1 ? 1 : criteria.getPageNum();
        long pageSize = criteria.getPageSize() < 1 ? 10 : criteria.getPageSize();

        List<Criteria> andList = new ArrayList<>();
        andList.add(Criteria.where("invalid").is(0L));
        if (StringUtils.hasText(criteria.getFileName())) {
            andList.add(Criteria.where("file_name").regex(escapeRegex(criteria.getFileName().trim()), "i"));
        }
        if (StringUtils.hasText(criteria.getFileStorageMode())) {
            andList.add(Criteria.where("file_storage_mode").is(criteria.getFileStorageMode().trim().toLowerCase()));
        }
        if (StringUtils.hasText(criteria.getFileStorageStation())) {
            andList.add(Criteria.where("file_storage_station").is(criteria.getFileStorageStation().trim()));
        }

        Query query = new Query(new Criteria().andOperator(andList.toArray(new Criteria[0])));
        long total = mongoTemplate.count(query, SysFilesDocument.class);

        query.with(Sort.by(Sort.Direction.DESC, "create_time"));
        query.skip((pageNum - 1) * pageSize).limit((int) pageSize);
        // 列表不回传路径等敏感/大字段时可再收窄；此处与 MySQL 列表字段对齐
        query.fields()
                .include("id")
                .include("create_time")
                .include("update_time")
                .include("file_name")
                .include("file_type")
                .include("file_label")
                .include("file_size")
                .include("file_storage_mode")
                .include("file_storage_station")
                .include("public_access")
                .include("create_id");

        List<FileMetadata> records = mongoTemplate.find(query, SysFilesDocument.class).stream()
                .map(MongoFileMetadataRepository::toMetadata)
                .collect(Collectors.toList());
        return new PageResult<>(records, total, pageNum, pageSize);
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
        Query query = Query.query(Criteria.where("id").in(ids).and("invalid").is(0L));
        return mongoTemplate.find(query, SysFilesDocument.class).stream()
                .map(MongoFileMetadataRepository::toMetadata)
                .collect(Collectors.toList());
    }

    private static String escapeRegex(String raw) {
        return raw.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("|", "\\|");
    }
}
