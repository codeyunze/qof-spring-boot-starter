package io.github.codeyunze.persistence.mongo.internal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * MongoDB 文件元数据文档（模块内部，不作为 Public API）。
 * <p>
 * 集合名 {@code sys_files}，字段语义与 MySQL {@code sys_files} 对齐，便于双端切换。
 */
@Document(collection = "sys_files")
@CompoundIndexes({
        @CompoundIndex(name = "idx_sys_files_invalid_ctime", def = "{'invalid': 1, 'create_time': -1}"),
        @CompoundIndex(name = "idx_sys_files_mode_station", def = "{'file_storage_mode': 1, 'file_storage_station': 1}")
})
public class SysFilesDocument {

    /**
     * 主键标识（与业务 fileId / 雪花 ID 一致）
     */
    @Id
    private Long id;

    /**
     * 创建时间
     */
    @Field("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field("update_time")
    private LocalDateTime updateTime;

    /**
     * 数据是否有效：0 数据有效；非 0（如删除时间戳）表示逻辑删除
     */
    @Field("invalid")
    private Long invalid = 0L;

    /**
     * 文件名称
     */
    @Indexed
    @Field("file_name")
    private String fileName;

    /**
     * 文件路径 / 对象 Key
     */
    @Field("file_path")
    private String filePath;

    /**
     * 文件类型（MIME）
     */
    @Field("file_type")
    private String fileType;

    /**
     * 文件标签
     */
    @Field("file_label")
    private String fileLabel;

    /**
     * 文件大小（字节）
     */
    @Field("file_size")
    private Long fileSize;

    /**
     * 存储模式（local/cos/oss/rustfs）
     */
    @Field("file_storage_mode")
    private String fileStorageMode;

    /**
     * 文件存储站
     */
    @Field("file_storage_station")
    private String fileStorageStation;

    /**
     * 是否公开访问：1-公开，0-不公开
     */
    @Field("public_access")
    private Integer publicAccess;

    /**
     * 创建者 ID
     */
    @Field("create_id")
    private Long createId;

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

    public Long getInvalid() {
        return invalid;
    }

    public void setInvalid(Long invalid) {
        this.invalid = invalid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public Integer getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Integer publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }
}
