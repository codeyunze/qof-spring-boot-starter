package io.github.codeyunze.core.rustfs;

/**
 * RustFS-对象存储的属性配置信息
 *
 * @author 高晗
 * @since 2025/1/12
 */
public class RustfsQofConfig {

    /**
     * 文件存储路径
     */
    private String filepath;

    /**
     * RustFS访问密钥
     */
    private String accessKey;

    /**
     * RustFS密钥Secret
     */
    private String secretKey;

    /**
     * 存储桶名称（Bucket）
     */
    private String bucketName;

    /**
     * RustFS服务端点（Endpoint）
     * 例如：http://localhost:9000
     */
    private String endpoint;

    /**
     * 区域（Region）
     * 例如：us-east-1
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
