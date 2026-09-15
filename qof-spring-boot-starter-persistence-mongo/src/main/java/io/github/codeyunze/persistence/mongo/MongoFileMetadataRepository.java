package io.github.codeyunze.persistence.mongo;

import io.github.codeyunze.persistence.mongo.internal.SysFilesDocument;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.metadata.FileMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * MongoDB 元数据仓储实现（逻辑删除：invalid=删除时间戳）。
 */
public class MongoFileMetadataRepository implements FileMetadataRepository {

    private final MongoTemplate mongoTemplate;

    public MongoFileMetadataRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String type() {
        return "mongo";
    }

    @Override
    public Optional<FileMetadata> findById(Long fileId) {
        if (fileId == null) {
            return Optional.empty();
        }
        Query query = Query.query(Criteria.where("id").is(fileId).and("invalid").is(0L));
        SysFilesDocument doc = mongoTemplate.findOne(query, SysFilesDocument.class);
        return Optional.ofNullable(doc).map(MongoFileMetadataRepository::toMetadata);
    }

    @Override
    public Long save(FileMetadata metadata) {
        LocalDateTime now = LocalDateTime.now();
        SysFilesDocument doc = toDocument(metadata);
        if (doc.getCreateTime() == null) {
            doc.setCreateTime(now);
        }
        doc.setUpdateTime(now);
        doc.setInvalid(0L);
        mongoTemplate.insert(doc);
        return doc.getId();
    }

    @Override
    public void update(FileMetadata metadata) {
        SysFilesDocument doc = toDocument(metadata);
        doc.setUpdateTime(LocalDateTime.now());
        Query query = Query.query(Criteria.where("id").is(doc.getId()).and("invalid").is(0L));
        Update update = new Update()
                .set("file_name", doc.getFileName())
                .set("file_path", doc.getFilePath())
                .set("file_type", doc.getFileType())
                .set("file_label", doc.getFileLabel())
                .set("file_size", doc.getFileSize())
                .set("file_storage_mode", doc.getFileStorageMode())
                .set("file_storage_station", doc.getFileStorageStation())
                .set("public_access", doc.getPublicAccess())
                .set("create_id", doc.getCreateId())
                .set("update_time", doc.getUpdateTime());
        mongoTemplate.updateFirst(query, update, SysFilesDocument.class);
    }

    @Override
    public boolean deleteById(Long fileId) {
        if (fileId == null) {
            return false;
        }
        long deletedAt = System.currentTimeMillis();
        Query query = Query.query(Criteria.where("id").is(fileId).and("invalid").is(0L));
        Update update = new Update()
                .set("invalid", deletedAt)
                .set("update_time", LocalDateTime.now());
        return mongoTemplate.updateFirst(query, update, SysFilesDocument.class).getModifiedCount() > 0;
    }

    static FileMetadata toMetadata(SysFilesDocument doc) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(doc.getId());
        metadata.setFileName(doc.getFileName());
        metadata.setFilePath(doc.getFilePath());
        metadata.setFileType(doc.getFileType());
        metadata.setFileLabel(doc.getFileLabel());
        metadata.setFileSize(doc.getFileSize());
        metadata.setFileStorageMode(doc.getFileStorageMode());
        metadata.setFileStorageStation(doc.getFileStorageStation());
        metadata.setPublicAccess(doc.getPublicAccess());
        metadata.setCreateId(doc.getCreateId());
        metadata.setCreateTime(doc.getCreateTime());
        metadata.setUpdateTime(doc.getUpdateTime());
        return metadata;
    }

    private static SysFilesDocument toDocument(FileMetadata metadata) {
        SysFilesDocument doc = new SysFilesDocument();
        doc.setId(metadata.getFileId());
        doc.setFileName(metadata.getFileName());
        doc.setFilePath(metadata.getFilePath());
        doc.setFileType(metadata.getFileType());
        doc.setFileLabel(metadata.getFileLabel());
        doc.setFileSize(metadata.getFileSize());
        doc.setFileStorageMode(metadata.getFileStorageMode());
        doc.setFileStorageStation(metadata.getFileStorageStation());
        doc.setPublicAccess(metadata.getPublicAccess());
        doc.setCreateId(metadata.getCreateId());
        doc.setCreateTime(metadata.getCreateTime());
        doc.setUpdateTime(metadata.getUpdateTime());
        return doc;
    }
}
