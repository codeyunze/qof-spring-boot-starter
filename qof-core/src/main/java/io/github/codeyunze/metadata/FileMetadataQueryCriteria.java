package io.github.codeyunze.metadata;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 文件元数据分页查询条件。
 *
 * @author yunze
 * @since 18.0.0
 */
public class FileMetadataQueryCriteria {

    /**
     * 页码，从 1 开始；默认 1。
     */
    private long pageNum = 1L;

    /**
     * 每页条数；默认 10。
     */
    private long pageSize = 10L;

    /**
     * 文件名（模糊匹配），可选。
     */
    private String fileName;

    /**
     * 存储模式精确匹配（local / cos / oss / s3 等），可选。
     */
    private String fileStorageMode;

    /**
     * 存储站别名精确匹配，可选。
     */
    private String fileStorageStation;

    /**
     * 创建时间-开始
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTimeFrom;

    /**
     * 创建时间-结束
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTimeTo;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileStorageMode() {
        return fileStorageMode;
    }

    public void setFileStorageMode(String fileStorageMode) {
        this.fileStorageMode = fileStorageMode;
    }

    public String getFileStorageStation() {
        return fileStorageStation;
    }

    public void setFileStorageStation(String fileStorageStation) {
        this.fileStorageStation = fileStorageStation;
    }

    public LocalDateTime getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(LocalDateTime createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public LocalDateTime getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(LocalDateTime createTimeTo) {
        this.createTimeTo = createTimeTo;
    }
}
