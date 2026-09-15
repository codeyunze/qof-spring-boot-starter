package io.github.codeyunze.spi;

import io.github.codeyunze.spi.metadata.FileMetadata;
import io.github.codeyunze.spi.metadata.FileMetadataQueryCriteria;
import io.github.codeyunze.spi.metadata.PageResult;

/**
 * 文件元数据分页查询（可选 SPI）。
 * <p>
 * 无实现时不影响上传 / 下载 / 删除；列表接口应返回明确错误。
 *
 * @author yunze
 * @since 18.0.0
 */
public interface FileMetadataQuery {

    PageResult<FileMetadata> page(FileMetadataQueryCriteria criteria);
}
