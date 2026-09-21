package io.github.codeyunze.metadata;

import java.util.List;

/**
 * 文件元数据分页查询（可选 SPI）。
 * <p>
 * 无实现时不影响上传 / 下载 / 删除；列表接口应返回明确错误。
 *
 * @author yunze
 * @since 18.0.0
 */
public interface FileMetadataQuery {

    /**
     * 按条件分页查询文件元数据。
     * <p>
     * 用于列表展示：页码从 1 开始，可按文件名模糊匹配、存储模式 / 存储站精确过滤，
     * 以及创建时间区间（{@code createTimeFrom} / {@code createTimeTo}，闭区间）。
     * 列表场景默认不回传 {@code filePath} 等存储路径字段；需要路径时请使用 {@link #listByIds(List)}。
     *
     * @param criteria 查询条件，不应为 {@code null}
     * @return 分页结果；无匹配时 {@code records} 为空列表
     */
    PageResult<FileMetadata> page(FileMetadataQueryCriteria criteria);

    /**
     * 按文件 ID 批量查询完整元数据（含 {@code filePath}）。
     * <p>
     * 供预览地址拼接等需要路径的场景使用。返回集合不含不存在或已删除的记录，且不保证与入参顺序一致。
     *
     * @param fileIds 文件 ID，{@code null} 元素会被忽略
     * @return 命中的元数据列表，不会为 {@code null}
     */
    List<FileMetadata> listByIds(List<Long> fileIds);
}
