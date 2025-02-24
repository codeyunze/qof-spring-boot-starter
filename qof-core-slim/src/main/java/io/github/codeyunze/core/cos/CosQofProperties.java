package io.github.codeyunze.core.cos;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 腾讯云-COS对象存储属性配置信息
 *
 * @author yunze
 * @since 2025/2/16 16:28
 */
@ConfigurationProperties(
        // qof.cos
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS
)
public class CosQofProperties extends CosQofConfig {

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
}


