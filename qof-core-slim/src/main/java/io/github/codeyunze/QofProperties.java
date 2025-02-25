package io.github.codeyunze;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置属性
 * @author yunze
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

    public boolean isPersistentEnable() {
        return persistentEnable;
    }

    public void setPersistentEnable(boolean persistentEnable) {
        this.persistentEnable = persistentEnable;
    }
}
