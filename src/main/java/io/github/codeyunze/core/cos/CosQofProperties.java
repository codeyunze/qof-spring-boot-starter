package io.github.codeyunze.core.cos;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 腾讯云-COS对象存储属性配置信息
 *
 * @author yunze
 * @since 2025/2/16 16:28
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(
        // qof.cos
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS
)
public class CosQofProperties extends CosQofModel {

    /**
     * 是否启用腾讯云COS对象存储
     */
    private boolean enable;

    /**
     * 多个COS配置信息
     * Map<Bean名称, COS配置信息>
     */
    Map<String, CosQofModel> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, CosQofModel> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, CosQofModel> multiple) {
        this.multiple = multiple;
    }
}


