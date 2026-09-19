package io.github.codeyunze.storage.s3;

import io.github.codeyunze.exception.StorageConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * S3 兼容对象存储属性配置信息。
 *
 * @author 高晗
 * @since 2025/1/12
 */
@ConfigurationProperties(prefix = "qof.s3")
public class S3QofProperties extends S3QofConfig implements InitializingBean {

    /**
     * 是否启用 S3 兼容对象存储
     */
    private boolean enable;

    /**
     * 默认使用的存储站
     * <br>
     * 当操作没有指定存储站时，会使用指定的默认存储站
     */
    private String defaultStorageStation;

    /**
     * 多个 S3 配置信息
     * Map&lt;文件存储站名, S3配置信息&gt;
     */
    Map<String, S3QofConfig> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, S3QofConfig> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, S3QofConfig> multiple) {
        this.multiple = multiple;
    }

    public String getDefaultStorageStation() {
        return defaultStorageStation;
    }

    public void setDefaultStorageStation(String defaultStorageStation) {
        this.defaultStorageStation = defaultStorageStation;
    }

    @Override
    public void afterPropertiesSet() {
        if (!this.enable) {
            return;
        }

        if (CollectionUtils.isEmpty(this.multiple)) {
            if (!StringUtils.hasText(this.getAccessKey())) {
                throw new StorageConfigurationException("缺少S3访问密钥配置信息[qof.s3.access-key]");
            } else if (!StringUtils.hasText(this.getSecretKey())) {
                throw new StorageConfigurationException("缺少S3密钥配置信息[qof.s3.secret-key]");
            } else if (!StringUtils.hasText(this.getBucketName())) {
                throw new StorageConfigurationException("缺少S3存储桶配置信息[qof.s3.bucket-name]");
            } else if (!StringUtils.hasText(this.getEndpoint())) {
                throw new StorageConfigurationException("缺少S3服务端点配置信息[qof.s3.endpoint]");
            }
        } else {
            if (!StringUtils.hasText(this.getAccessKey())) {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKey())) {
                        throw new StorageConfigurationException("缺少S3访问密钥配置信息[qof.s3.multiple." + entry.getKey() + ".access-key]");
                    }
                }
            } else {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKey())) {
                        entry.getValue().setAccessKey(this.getAccessKey());
                    }
                }
            }

            if (!StringUtils.hasText(this.getSecretKey())) {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        throw new StorageConfigurationException("缺少S3密钥配置信息[qof.s3.multiple." + entry.getKey() + ".secret-key]");
                    }
                }
            } else {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        entry.getValue().setSecretKey(this.getSecretKey());
                    }
                }
            }

            if (!StringUtils.hasText(this.getEndpoint())) {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        throw new StorageConfigurationException("缺少S3服务端点配置信息[qof.s3.multiple." + entry.getKey() + ".endpoint]");
                    }
                }
            } else {
                for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        entry.getValue().setEndpoint(this.getEndpoint());
                    }
                }
            }

            for (Map.Entry<String, S3QofConfig> entry : this.multiple.entrySet()) {
                if (!StringUtils.hasText(entry.getValue().getBucketName())) {
                    throw new StorageConfigurationException("缺少S3存储桶配置信息[qof.s3.multiple." + entry.getKey() + ".bucket-name]");
                }
            }
        }
    }
}
