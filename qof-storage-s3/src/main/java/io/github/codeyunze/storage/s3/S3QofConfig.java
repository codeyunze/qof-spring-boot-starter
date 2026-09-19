package io.github.codeyunze.storage.s3;

/**
 * S3 兼容对象存储的属性配置信息（RustFS / MinIO 等）。
 *
 * @author 高晗
 * @since 2025/1/12
 */
public class S3QofConfig {

    /**
     * 文件存储路径
     */
    private String filepath;

    /**
     * 访问密钥。YAML 可写 {@code access-key} 或 {@code secret-id}。
     */
    private String accessKey;

    /**
     * 密钥。YAML 写 {@code secret-key}。
     */
    private String secretKey;

    /**
     * 存储桶名称（Bucket）
     */
    private String bucketName;

    /**
     * S3 兼容服务端点（Endpoint）
     * 例如：http://localhost:9000
     */
    private String endpoint;

    /**
     * 区域（Region）。
     * <p>
     * RustFS / MinIO 等兼容存储通常无需配置；未填写时默认 {@code us-east-1}，
     * 仅用于满足 AWS SDK 构建客户端的必填约束。
     */
    private String region;

    /**
     * 文件预览地址
     */
    private String previewAddress;

    /**
     * 单链接限速（单位：字节/秒）
     * 设置为0或null表示不限速
     */
    private Long trafficLimit;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * {@code secret-id} 作为 {@code access-key} 的别名。
     */
    public String getSecretId() {
        return accessKey;
    }

    public void setSecretId(String secretId) {
        if (this.accessKey == null || this.accessKey.isBlank()) {
            this.accessKey = secretId;
        }
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPreviewAddress() {
        return previewAddress;
    }

    public void setPreviewAddress(String previewAddress) {
        this.previewAddress = previewAddress;
    }

    public Long getTrafficLimit() {
        return trafficLimit;
    }

    public void setTrafficLimit(Long trafficLimit) {
        this.trafficLimit = trafficLimit;
    }
}
