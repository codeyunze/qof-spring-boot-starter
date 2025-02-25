package io.github.codeyunze.core.local;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 本地文件存储属性配置信息
 *
 * @author yunze
 * @since 2025/2/16 16:28
 */
@ConfigurationProperties(prefix = "qof.local")
public class LocalQofProperties extends LocalQofConfig implements InitializingBean {

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
     * 多个文件存储路径配置信息
     * Map<Bean名称, 本地文件存储的属性配置信息>
     */
    Map<String, LocalQofConfig> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, LocalQofConfig> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, LocalQofConfig> multiple) {
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
            if (!StringUtils.hasText(this.getFilepath())) {
                throw new RuntimeException("缺少文件存储路径配置信息[qof.local.filepath]");
            }
        } else {
            if (StringUtils.hasText(this.getFilepath())) {
                for (Map.Entry<String, LocalQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getFilepath())) {
                        throw new RuntimeException("缺少文件存储路径配置信息[qof.local.multiple." + entry.getKey() + ".filepath]");
                    }
                }
            } else {
                for (Map.Entry<String, LocalQofConfig> entry : this.multiple.entrySet()) {
                    if (!StringUtils.hasText(entry.getValue().getFilepath())) {
                        entry.getValue().setFilepath(this.getFilepath());
                    }
                }
            }
        }
    }
}


