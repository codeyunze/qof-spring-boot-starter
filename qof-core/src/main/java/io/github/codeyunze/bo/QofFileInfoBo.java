package io.github.codeyunze.bo;

import io.github.codeyunze.core.QofFileOperationBase;

import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author 高晗
 * @since 2025-02-16 15:43:41
 */
public class QofFileInfoBo<T> extends QofFileOperationBase {

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文件名称
     * <p>
     * 例如： 靓图.png
     */
    private String fileName;

    /**
     * 文件类型
     * <br>
     * 例如image/png、image/jpeg、application/pdf、video/mp4等
     * <p>
     * 例如： image/png
     */
    private String fileType;

    /**
     * 文件标签
     * <br>
     * 给文件添加标签（如：证件照、报告、审核表、图标等）
     * <p>
     * 例如： 人像
     */
    private String fileLabel;

    /**
     * 文件大小(单位byte字节)
     * <p>
     * 例如： 1024
     */
    private Long fileSize = 0L;

    /**
     * 是否公开访问：1-公开，0-不公开
     */
    private Integer publicAccess;

    /**
     * 创建者ID
     */
    private Long createId;

    /**
     * 扩展数据对象
     */
    private T extendObject;

    public T getExtendObject() {
        return extendObject;
    }

    public void setExtendObject(T extendObject) {
        this.extendObject = extendObject;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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

