package io.github.codeyunze.spi;

import java.util.Optional;

/**
 * 文件元数据仓储 SPI。
 * <p>
 * 将「是否落库」从存储实现中剥离：默认可用 Noop；需要 fileId 语义时再启用 MyBatis 实现。
 *
 * @author yunze
 * @since 17.1.0
 */
public interface FileMetadataRepository {

    /**
     * 按文件 Id 查询元数据。
     *
     * @param fileId 文件 Id
     * @return 元数据；不存在则为 empty
     */
    Optional<Object> findById(Long fileId);

    /**
     * 写入元数据。
     *
     * @param metadata 元数据对象
     * @return 主键 Id
     */
    Long insert(Object metadata);

    /**
     * 更新元数据。
     *
     * @param metadata 元数据对象
     */
    void update(Object metadata);

    /**
     * 逻辑删除。
     *
     * @param fileId 文件 Id
     * @return 是否成功
     */
    boolean logicDelete(Long fileId);
}
