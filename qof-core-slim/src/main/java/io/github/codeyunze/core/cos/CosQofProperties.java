package io.github.codeyunze.core.cos;

import io.github.codeyunze.core.local.LocalQofConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 腾讯云-COS对象存储属性配置信息
 *
 * @author 高晗
 * @since 2025/2/16 16:28
 */
@ConfigurationProperties(
        // qof.cos
        prefix = "qof.cos"
)
public class CosQofProperties extends CosQofConfig implements InitializingBean {

    /**
     * 是否启用腾讯云COS对象存储
     */
    private boolean enable;

    /**
     * 默认使用的存储站
     * <br>
     * 当操作没有指定存储站时，会使用指定的默认存储站
     */
    private String defaultStorageStation;

    /**
     * 多个COS配置信息
     * Map<文件存储站名, COS配置信息>
     */
    Map<String, CosQofConfig> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, CosQofConfig> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, CosQofConfig> multiple) {
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
            if (!StringUtils.hasText(this.getSecretId())) {
                throw new RuntimeException("缺少COS秘钥配置信息[qof.cos.secret-id]");
            } else if (!StringUtils.hasText(this.getSecretKey())) {
                throw new RuntimeException("缺少COS秘钥配置信息[qof.cos.secret-key]");
            } else if (!StringUtils.hasText(this.getBucketName())) {
                throw new RuntimeException("缺少COS存储桶配置信息[qof.cos.bucket-name]");
            } else if (!StringUtils.hasText(this.getRegion())) {
                throw new RuntimeException("缺少COS存储桶地域配置信息[qof.cos.region]");
            }
        } else {
            if (!StringUtils.hasText(this.getSecretId())) {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretId())) {
                        throw new RuntimeException("缺少COS秘钥配置信息[qof.cos.multiple." + entry.getKey() + ".secret-id]");
                    }
                }
            } else {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretId())) {
                        entry.getValue().setSecretId(this.getSecretId());
                    }
                }
            }

            if (!StringUtils.hasText(this.getSecretKey())) {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        throw new RuntimeException("缺少COS秘钥配置信息[qof.cos.multiple." + entry.getKey() + ".secret-key]");
                    }
                }
            } else {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getSecretKey())) {
                        entry.getValue().setSecretKey(this.getSecretKey());
                    }
                }
            }

            if (!StringUtils.hasText(this.getRegion())) {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getRegion())) {
                        throw new RuntimeException("缺少COS存储桶地域配置信息[qof.cos.multiple." + entry.getKey() + ".region]");
                    }
                }
            } else {
                for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getRegion())) {
                        entry.getValue().setSecretKey(this.getRegion());
                    }
                }
            }

            for (Map.Entry<String, CosQofConfig> entry : this.multiple.entrySet()) {
                if (!StringUtils.hasText(entry.getValue().getBucketName())) {
                    throw new RuntimeException("缺少COS存储桶配置信息[qof.cos.multiple." + entry.getKey() + ".bucket-name]");
                }
            }
        }

    }
}


