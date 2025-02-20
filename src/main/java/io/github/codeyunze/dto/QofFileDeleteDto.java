package io.github.codeyunze.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 删除文件信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
public class QofFileDeleteDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 文件唯一Id
     */
    @NotNull(message = "文件id不能为空")
    private Long fileId;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应{@link io.github.codeyunze.enums.QofStorageModeEnum}
     */
    @NotBlank(message = "文件存储模式不能为空")
    private String fileStorageMode;

    /**
     * 文件存储桶
     */
    private String fileStorageBucket;

    public @NotNull(message = "文件id不能为空") Long getFileId() {
        return fileId;
    }

    public void setFileId(@NotNull(message = "文件id不能为空") Long fileId) {
        this.fileId = fileId;
    }

    public @NotBlank(message = "文件存储模式不能为空") String getFileStorageMode() {
        return fileStorageMode;
    }

    public void setFileStorageMode(@NotBlank(message = "文件存储模式不能为空") String fileStorageMode) {
        this.fileStorageMode = fileStorageMode;
    }

    public String getFileStorageBucket() {
        return fileStorageBucket;
    }

    public void setFileStorageBucket(String fileStorageBucket) {
        this.fileStorageBucket = fileStorageBucket;
    }
}

