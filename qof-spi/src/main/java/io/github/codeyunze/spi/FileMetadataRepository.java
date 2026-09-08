package io.github.codeyunze.spi;

import io.github.codeyunze.spi.metadata.FileMetadata;

import java.util.Optional;

/**
 * 文件元数据仓储（必选 SPI）。
 *
 * @author yunze
 * @since 17.1.0
 */
public interface FileMetadataRepository {

    /**
     * 实现标识（如 {@code mysql}、{@code custom:acme}），仅用于日志 / 互斥诊断。
     */
    String type();

    Optional<FileMetadata> findById(Long fileId);

    /**
     * 新增元数据，返回 fileId（通常与入参一致）。
     */
    Long save(FileMetadata metadata);

    void update(FileMetadata metadata);

    /**
     * 逻辑删除；true 表示元数据侧删除成功。
     */
    boolean deleteById(Long fileId);
}
