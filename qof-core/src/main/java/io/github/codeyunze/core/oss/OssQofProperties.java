package io.github.codeyunze.core.oss;

import io.github.codeyunze.exception.StorageConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 阿里云-OSS对象存储属性配置信息
 *
 * @author 高晗
 * @since 2025/2/18
 */
@ConfigurationProperties(prefix = "qof.oss")
public class OssQofProperties extends OssQofConfig implements InitializingBean {

    /**
     * 是否启用阿里云OSS对象存储
     */
    private boolean enable;

    /**
     * 默认使用的存储站
     * <br>
     * 当操作没有指定存储站时，会使用指定的默认存储站
     */
    private String defaultStorageStation;

    /**
     * 多个OSS配置信息
     * Map<文件存储站名, OSS配置信息>
     */
    Map<String, OssQofConfig> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, OssQofConfig> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, OssQofConfig> multiple) {
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

        // 检查/补全配置信息是否齐全
        if (CollectionUtils.isEmpty(this.multiple)) {
            if (!StringUtils.hasText(this.getAccessKeyId())) {
                throw new StorageConfigurationException("缺少OSS访问密钥配置信息[qof.oss.access-key-id]");
            } else if (!StringUtils.hasText(this.getAccessKeySecret())) {
                throw new StorageConfigurationException("缺少OSS访问密钥配置信息[qof.oss.access-key-secret]");
            } else if (!StringUtils.hasText(this.getBucketName())) {
                throw new StorageConfigurationException("缺少OSS存储桶配置信息[qof.oss.bucket-name]");
            } else if (!StringUtils.hasText(this.getEndpoint())) {
                throw new StorageConfigurationException("缺少OSS地域节点配置信息[qof.oss.endpoint]");
            }
        } else {
            if (!StringUtils.hasText(this.getAccessKeyId())) {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKeyId())) {
                        throw new StorageConfigurationException("缺少OSS访问密钥配置信息[qof.oss.multiple." + entry.getKey() + ".access-key-id]");
                    }
                }
            } else {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKeyId())) {
                        entry.getValue().setAccessKeyId(this.getAccessKeyId());
                    }
                }
            }

            if (!StringUtils.hasText(this.getAccessKeySecret())) {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKeySecret())) {
                        throw new StorageConfigurationException("缺少OSS访问密钥配置信息[qof.oss.multiple." + entry.getKey() + ".access-key-secret]");
                    }
                }
            } else {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKeySecret())) {
                        entry.getValue().setAccessKeySecret(this.getAccessKeySecret());
                    }
                }
            }

            if (!StringUtils.hasText(this.getEndpoint())) {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        throw new StorageConfigurationException("缺少OSS地域节点配置信息[qof.oss.multiple." + entry.getKey() + ".endpoint]");
                    }
                }
            } else {
                for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        entry.getValue().setEndpoint(this.getEndpoint());
                    }
                }
            }

            for (Map.Entry<String, OssQofConfig> entry : this.multiple.entrySet()) {
                if (!StringUtils.hasText(entry.getValue().getBucketName())) {
                    throw new StorageConfigurationException("缺少OSS存储桶配置信息[qof.oss.multiple." + entry.getKey() + ".bucket-name]");
                }
            }
        }
    }
}

