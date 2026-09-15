package io.github.codeyunze.spi.metadata;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果。
 *
 * @param <T> 记录类型
 * @author yunze
 * @since 18.0.0
 */
public class PageResult<T> {

    /**
     * 当前页数据列表。
     */
    private List<T> records;

    /**
     * 符合条件的总记录数。
     */
    private long total;

    /**
     * 当前页码（从 1 开始）。
     */
    private long pageNum;

    /**
     * 每页条数。
     */
    private long pageSize;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(List<T> records, long total, long pageNum, long pageSize) {
        this.records = records != null ? records : Collections.emptyList();
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records != null ? records : Collections.emptyList();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
