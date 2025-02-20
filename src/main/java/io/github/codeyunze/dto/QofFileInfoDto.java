package io.github.codeyunze.dto;

import io.github.codeyunze.core.cos.QofCosProperties;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
public class QofFileInfoDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 文件唯一Id [非必传]
     * <br>
     * 如果不传fileId，则会自动生成一个id
     */
    private Long fileId;

    /**
     * 文件名称
     * <p>
     * 例如： 靓图.png
     */
    @Length(max = 36, message = "文件名称超过最大长度限制")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件存储目录地址 [非必传]
     * <br>
     * 真实存储路径地址为 {@link QofCosProperties#getFilepath()} + {@link QofFileInfoDto#getDirectoryAddress()}
     * <br>
     * 例如：文件完整的存储路径为 '/files/business/20250201/靓图.png', 则
     * '/files'为{@link QofCosProperties#getFilepath()},
     * '/business/20250201'为使用者传入的{@link QofFileInfoDto#getDirectoryAddress()},
     * '靓图.png'为文件名称{@link QofFileInfoDto#getFileName()}
     * <p>
     * 例如： /business/20250201
     */
    @Length(max = 255, message = "文件路径超过最大长度限制")
    @NotBlank(message = "文件路径不能为空")
    private String directoryAddress;

    /**
     * 文件类型
     * <br>
     * 例如image/png、image/jpeg、application/pdf、video/mp4等
     * <p>
     * 例如： image/png
     */
    @Length(max = 36, message = "文件类型超过最大长度限制")
    private String fileType;

    /**
     * 文件标签
     * <br>
     * 给文件添加标签（如：证件照、报告、审核表、图标等）
     * <p>
     * 例如： 人像
     */
    @Length(max = 36, message = "文件标签超过最大长度限制")
    private String fileLabel;

    /**
     * 文件大小(单位byte字节) [非必传]
     */
    private Long fileSize = 0L;

    /**
     * 文件存储路径 [非必传]
     * <br>
     * 文件存储路径组成为{@link QofCosProperties#getFilepath()}
     * + {@link QofFileInfoDto#getDirectoryAddress()}
     * + '/'
     * + {@link QofFileInfoDto#getFileId()}
     * + ( {@link QofFileInfoDto#getFileName()}的后缀 )
     * <p>
     * 例如： /files/business/20250201/1891054775523446784.png
     */
    private String filePath;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应{@link io.github.codeyunze.enums.QofStorageModeEnum}
     */
    private String fileStorageMode;

    /**
     * 文件存储桶
     */
    private String fileStorageBucket;


    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public @Length(max = 36, message = "文件名称超过最大长度限制") @NotBlank(message = "文件名称不能为空") String getFileName() {
        return fileName;
    }

    public void setFileName(@Length(max = 36, message = "文件名称超过最大长度限制") @NotBlank(message = "文件名称不能为空") String fileName) {
        this.fileName = fileName;
    }

    public @Length(max = 255, message = "文件路径超过最大长度限制") @NotBlank(message = "文件路径不能为空") String getDirectoryAddress() {
        return directoryAddress;
    }

    public void setDirectoryAddress(@Length(max = 255, message = "文件路径超过最大长度限制") @NotBlank(message = "文件路径不能为空") String directoryAddress) {
        this.directoryAddress = directoryAddress;
    }

    public @Length(max = 36, message = "文件类型超过最大长度限制") String getFileType() {
        return fileType;
    }

    public void setFileType(@Length(max = 36, message = "文件类型超过最大长度限制") String fileType) {
        this.fileType = fileType;
    }

    public @Length(max = 36, message = "文件标签超过最大长度限制") String getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(@Length(max = 36, message = "文件标签超过最大长度限制") String fileLabel) {
        this.fileLabel = fileLabel;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileStorageMode() {
        return fileStorageMode;
    }

    public void setFileStorageMode(String fileStorageMode) {
        this.fileStorageMode = fileStorageMode;
    }

    public String getFileStorageBucket() {
        return fileStorageBucket;
    }

    public void setFileStorageBucket(String fileStorageBucket) {
        this.fileStorageBucket = fileStorageBucket;
    }
}

