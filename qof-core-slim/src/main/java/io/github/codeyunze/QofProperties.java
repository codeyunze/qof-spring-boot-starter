package io.github.codeyunze;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件配置属性
 * @author 高晗
 * @since 2025/2/16 16:28
 */
@Configuration
@ConfigurationProperties(
        prefix = "qof"
)
public class QofProperties {

    /**
     * 是否开始文件信息数据持久化
     */
    boolean persistentEnable;

    /**
     * 文件大小限制（单位：字节）
     * 默认值：100MB (104857600字节)
     * 设置为0或负数表示不限制
     */
    private long maxFileSize = 104857600L;

    /**
     * 支持预览的文件类型列表
     * 默认支持：image/png, image/jpeg, application/pdf
     */
    private List<String> previewSupportedTypes = new ArrayList<String>() {{
        add("image/png");
        add("image/jpeg");
        add("application/pdf");
    }};

    public boolean isPersistentEnable() {
        return persistentEnable;
    }

    public void setPersistentEnable(boolean persistentEnable) {
        this.persistentEnable = persistentEnable;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getPreviewSupportedTypes() {
        return previewSupportedTypes;
    }

    public void setPreviewSupportedTypes(List<String> previewSupportedTypes) {
        this.previewSupportedTypes = previewSupportedTypes;
    }
}
