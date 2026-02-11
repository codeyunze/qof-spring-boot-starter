package io.github.codeyunze.core.rustfs;

import io.github.codeyunze.exception.StorageConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * RustFS-对象存储属性配置信息
 *
 * @author 高晗
 * @since 2025/1/12
 */
@ConfigurationProperties(prefix = "qof.rustfs")
public class RustfsQofProperties extends RustfsQofConfig implements InitializingBean {

    /**
     * 是否启用RustFS对象存储
     */
    private boolean enable;

    /**
     * 默认使用的存储站
     * <br>
     * 当操作没有指定存储站时，会使用指定的默认存储站
     */
    private String defaultStorageStation;

    /**
     * 多个RustFS配置信息
     * Map<文件存储站名, RustFS配置信息>
     */
    Map<String, RustfsQofConfig> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, RustfsQofConfig> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, RustfsQofConfig> multiple) {
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
            if (!StringUtils.hasText(this.getAccessKey())) {
                throw new StorageConfigurationException("缺少RustFS访问密钥配置信息[qof.rustfs.access-key-id]");
            } else if (!StringUtils.hasText(this.getSecretKey())) {
                throw new StorageConfigurationException("缺少RustFS访问密钥配置信息[qof.rustfs.secret-access-key]");
            } else if (!StringUtils.hasText(this.getBucketName())) {
                throw new StorageConfigurationException("缺少RustFS存储桶配置信息[qof.rustfs.bucket-name]");
            } else if (!StringUtils.hasText(this.getEndpoint())) {
                throw new StorageConfigurationException("缺少RustFS服务端点配置信息[qof.rustfs.endpoint]");
            }
        } else {
            if (!StringUtils.hasText(this.getAccessKey())) {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKey())) {
                        throw new StorageConfigurationException("缺少RustFS访问密钥配置信息[qof.rustfs.multiple." + entry.getKey() + ".access-key-id]");
                    }
                }
            } else {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getAccessKey())) {
                        entry.getValue().setAccessKey(this.getAccessKey());
                    }
                }
            }

            if (!StringUtils.hasText(this.getSecretKey())) {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        throw new StorageConfigurationException("缺少RustFS访问密钥配置信息[qof.rustfs.multiple." + entry.getKey() + ".secret-access-key]");
                    }
                }
            } else {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        entry.getValue().setSecretKey(this.getSecretKey());
                    }
                }
            }

            if (!StringUtils.hasText(this.getEndpoint())) {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        throw new StorageConfigurationException("缺少RustFS服务端点配置信息[qof.rustfs.multiple." + entry.getKey() + ".endpoint]");
                    }
                }
            } else {
                for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getEndpoint())) {
                        entry.getValue().setEndpoint(this.getEndpoint());
                    }
                }
            }

            for (Map.Entry<String, RustfsQofConfig> entry : this.multiple.entrySet()) {
                if (!StringUtils.hasText(entry.getValue().getBucketName())) {
                    throw new StorageConfigurationException("缺少RustFS存储桶配置信息[qof.rustfs.multiple." + entry.getKey() + ".bucket-name]");
                }
            }
        }
    }
}
