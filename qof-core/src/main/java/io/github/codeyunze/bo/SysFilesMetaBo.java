package io.github.codeyunze.bo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件元数据信息（用于列表展示）
 * <p>
 * 注意：不包含 filePath，避免暴露存储路径。
 *
 * @author 高晗
 * @since 2025/12/17
 */
public class SysFilesMetaBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一Id
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型（image/png、image/jpeg）
     */
    private String fileType;

    /**
     * 文件标签
     */
    private String fileLabel;

    /**
     * 文件大小(单位byte字节)
     */
    private Long fileSize;

    /**
     * 文件存储模式(local、cos、oss)
     */
    private String fileStorageMode;

    /**
     * 文件存储站
     */
    private String fileStorageStation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(String fileLabel) {
        this.fileLabel = fileLabel;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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


