package io.github.codeyunze.storage.cos;

/**
 * 腾讯云-COS对象存储的属性配置信息
 *
 * @author 高晗
 * @since 2025/2/16 20:39
 */
public class CosQofConfig {

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
     * 存储桶名称
     */
    public String bucketName;

    /**
     * COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-guangzhou，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
     */
    private String region;

    /**
     * 文件预览地址
     */
    private String previewAddress;

    /**
     * 单链接限速（单位：字节/秒），默认8MB/s
     * 设置为0或null表示不限速
     */
    private Long trafficLimit;

    public String getPreviewAddress() {
        return previewAddress;
    }

    public void setPreviewAddress(String previewAddress) {
        this.previewAddress = previewAddress;
    }

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getTrafficLimit() {
        return trafficLimit;
    }

    public void setTrafficLimit(Long trafficLimit) {
        this.trafficLimit = trafficLimit;
    }
}
