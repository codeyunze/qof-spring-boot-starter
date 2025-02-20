package io.github.codeyunze.bo;

import io.github.codeyunze.core.cos.CosQofProperties;

import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
public class QofFileInfoBo {

    /**
     * 文件唯一id
     */
    private Long id;

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
     * 文件存储路径
     * <br>
     * 文件存储路径组成为{@link CosQofProperties#getFilepath()}
     * + {@link io.github.codeyunze.dto.QofFileInfoDto#getDirectoryAddress()}
     * + '/'
     * + {@link QofFileInfoBo#getId()}
     * + ( {@link QofFileInfoBo#getFileName()}的后缀 )
     * <p>
     * 例如： /files/business/20250201/1891054775523446784.png
     */
    private String filePath;

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
}

