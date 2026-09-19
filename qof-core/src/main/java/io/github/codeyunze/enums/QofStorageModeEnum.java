package io.github.codeyunze.enums;

import io.github.codeyunze.config.QofConstant;

/**
 * 文件存储模式
 *
 * @author 高晗
 * @since 2025/2/16 16:34
 */
public enum QofStorageModeEnum {

    /**
     * 本地存储模式
     */
    LOCAL(QofConstant.StorageMode.LOCAL),
    /**
     * 腾讯云-COS对象存储模式
     */
    COS(QofConstant.StorageMode.COS),
    /**
     * 阿里云-OSS对象存储模式
     */
    OSS(QofConstant.StorageMode.OSS),
    /**
     * S3 兼容对象存储模式（RustFS / MinIO 等）
     */
    S3(QofConstant.StorageMode.S3);

    /**
     * 文件存储模式
     */
    private final String mode;

    QofStorageModeEnum(String mode) {
        this.mode = mode;
    }

    /**
     * 获取文件存储模式
     * @return 文件存储模式
     */
    public String getMode() {
        return mode;
    }
}
