package io.github.codeyunze.dto;

import io.github.codeyunze.QofConstant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件下载信息
 *
 * @author 高晗
 * @since 2025-02-16 15:43:42
 */
public class QofFileUniversalDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一 id
     */
    @NotNull(message = "文件id不能为空")
    private Long fileId;

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


    public @NotNull(message = "文件id不能为空") Long getFileId() {
        return fileId;
    }

    public void setFileId(@NotNull(message = "文件id不能为空") Long fileId) {
        this.fileId = fileId;
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

