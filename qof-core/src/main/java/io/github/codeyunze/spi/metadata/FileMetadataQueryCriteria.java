package io.github.codeyunze.spi.metadata;

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
     * 存储模式精确匹配（local / cos / oss / rustfs 等），可选。
     */
    private String fileStorageMode;

    /**
     * 存储站别名精确匹配，可选。
     */
    private String fileStorageStation;

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
}
