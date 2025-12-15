package io.github.codeyunze.core;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.cos.CosQofProperties;

import java.io.Serializable;

/**
 * 文件操作的基础参数
 * <br>
 * 操作文件必要的参数信息
 *
 * @author 高晗
 * @since 2025-02-16 15:43:42
 */
public class QofFileOperationBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一Id
     */
    private Long fileId;

    /**
     * 文件存储模式(local、cos、oss)
     * <br>
     * 对应 {@link io.github.codeyunze.enums.QofStorageModeEnum}
     */
    private String fileStorageMode;

    /**
     * 存储别名
     * <br>
     * 对应
     */
    private String fileStorageStation;

    /**
     * 文件存储路径
     * <br>
     * 文件存储路径组成为{@link CosQofProperties#getFilepath()}
     * + {@link io.github.codeyunze.dto.QofFileInfoDto#getDirectoryAddress()}
     * + '/'
     * + {@link QofFileOperationBase#getFileId()}
     * + ( {@link QofFileInfoBo#getFileName()}的后缀 )
     * <p>
     * 例如： /files/business/20250201/1891054775523446784.png
     */
    private String filePath;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

