package io.github.codeyunze.core.oss;

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
     * OSS访问密钥Id（AccessKeyId）
     */
    private String accessKeyId;

    /**
     * OSS访问密钥Secret（AccessKeySecret）
     */
    private String accessKeySecret;

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

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
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

