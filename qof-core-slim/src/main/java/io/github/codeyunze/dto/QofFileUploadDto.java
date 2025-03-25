package io.github.codeyunze.dto;

import io.github.codeyunze.QofConstant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传信息
 *
 * @author 高晗
 * @since 2025-02-16 15:43:42
 */
public class QofFileUploadDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 文件名称
     * <p>
     * 例如： 靓图.png
     */
    @Length(max = 36, message = "文件名称超过最大长度限制")
    private String fileName;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应{@link io.github.codeyunze.enums.QofStorageModeEnum}
     */
    @Length(max = 10, message = "文件存储模式超过最大长度限制")
    @NotBlank(message = "文件存储模式不能为空")
    private String fileStorageMode;

    /**
     * 文件存储站
     */
    @Length(max = 36, message = "文件存储站超过最大长度限制")
    private String fileStorageStation = QofConstant.DEFAULT;

    public @Length(max = 36, message = "文件名称超过最大长度限制") String getFileName() {
        return fileName;
    }

    public void setFileName(@Length(max = 36, message = "文件名称超过最大长度限制") String fileName) {
        this.fileName = fileName;
    }

    public @Length(max = 10, message = "文件存储模式超过最大长度限制") @NotBlank(message = "文件存储模式不能为空") String getFileStorageMode() {
        return fileStorageMode;
    }

    public void setFileStorageMode(@Length(max = 10, message = "文件存储模式超过最大长度限制") @NotBlank(message = "文件存储模式不能为空") String fileStorageMode) {
        this.fileStorageMode = fileStorageMode;
    }

    public @Length(max = 36, message = "文件存储站超过最大长度限制") String getFileStorageStation() {
        return fileStorageStation;
    }

    public void setFileStorageStation(@Length(max = 36, message = "文件存储站超过最大长度限制") String fileStorageStation) {
        this.fileStorageStation = fileStorageStation;
    }
}

