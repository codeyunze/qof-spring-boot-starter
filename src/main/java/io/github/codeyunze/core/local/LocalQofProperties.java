package io.github.codeyunze.core.local;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 本地文件存储属性配置信息
 *
 * @author yunze
 * @since 2025/2/16 16:28
 */
@ConfigurationProperties(
        // qof.local
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.LOCAL
)
public class LocalQofProperties extends LocalQofModel {

    /**
     * 是否启用腾讯云COS对象存储
     */
    private boolean enable;

    /**
     * 多个文件存储路径配置信息
     * Map<Bean名称, 本地文件存储的属性配置信息>
     */
    Map<String, LocalQofModel> multiple;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, LocalQofModel> getMultiple() {
        return multiple;
    }

    public void setMultiple(Map<String, LocalQofModel> multiple) {
        this.multiple = multiple;
    }
}


