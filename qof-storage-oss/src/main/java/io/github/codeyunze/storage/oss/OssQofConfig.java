package io.github.codeyunze.storage.oss;

/**
 * 阿里云-OSS对象存储的属性配置信息
 *
 * @author 高晗
 * @since 2025/2/18
 */
public class OssQofConfig {

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
     * OSS地域节点（Endpoint）
     * 例如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

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

