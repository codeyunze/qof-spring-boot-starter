package io.github.codeyunze.spi.metadata;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件元数据中立模型（与具体库表/ORM 解耦）。
 *
 * @author yunze
 * @since 18.0.0
 */
public class FileMetadata {

    /**
     * 文件主键；上传前可由业务预传，未传则由 Client 雪花生成。
     * <p>
     * 下载、删除、鉴权均以此为查询键。
     */
    private Long fileId;

    /**
     * 原始文件名（含后缀），例如：靓图.png。
     * <p>
     * 常用于下载时 Content-Disposition 等场景。
     */
    private String fileName;

    /**
     * 对象存储侧完整相对路径 / key。
     * <p>
     * 通常由目录地址 + fileId + 后缀拼接而成。
     */
    private String filePath;

    /**
     * 文件 MIME 类型，例如：image/png、application/pdf。
     */
    private String fileType;

    /**
     * 业务标签（证件照、报告、审核表等），可选。
     */
    private String fileLabel;

    /**
     * 文件大小，单位：字节。
     */
    private Long fileSize;

    /**
     * 存储模式：local / cos / oss / rustfs 等。
     */
    private String fileStorageMode;

    /**
     * 存储站别名（多 bucket / 多目录场景）；为空时回落配置中的 default station。
     */
    private String fileStorageStation;

    /**
     * 是否公开访问：1-公开，0-私有。
     */
    private Integer publicAccess;

    /**
     * 创建者 ID；私有文件鉴权时使用。
     */
    private Long createId;

    /**
     * 创建时间；一般由仓储写入或回填。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间；一般由仓储写入或回填。
     */
    private LocalDateTime updateTime;

    /**
     * 扩展字段；官方核心链路可不读，业务 / 自建仓储可按需持久化。
     */
    private Map<String, Object> attributes = new HashMap<>();

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }
}
