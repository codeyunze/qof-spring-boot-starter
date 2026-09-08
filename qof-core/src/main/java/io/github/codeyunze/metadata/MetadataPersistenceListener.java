package io.github.codeyunze.metadata;

import io.github.codeyunze.spi.FileLifecycleListener;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.lifecycle.DeleteContext;
import io.github.codeyunze.spi.lifecycle.UploadContext;
import io.github.codeyunze.spi.metadata.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 官方元数据落库 Listener：上传后写入，删除前先删元数据。
 *
 * @author yunze
 * @since 18.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class MetadataPersistenceListener implements FileLifecycleListener {

    private static final Logger log = LoggerFactory.getLogger(MetadataPersistenceListener.class);

    private final FileMetadataRepository repository;

    public MetadataPersistenceListener(FileMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterUpload(UploadContext context) {
        FileMetadata metadata = context.getMetadata();
        Long fileId = repository.save(metadata);
        metadata.setFileId(fileId);
        log.debug("元数据已落库, fileId={}, type={}", fileId, repository.type());
    }

    @Override
    public boolean beforeDelete(DeleteContext context) {
        FileMetadata metadata = context.getMetadata();
        if (metadata == null || metadata.getFileId() == null) {
            return false;
        }
        boolean deleted = repository.deleteById(metadata.getFileId());
        if (!deleted) {
            log.warn("元数据删除失败, fileId={}", metadata.getFileId());
        }
        return deleted;
    }
}
