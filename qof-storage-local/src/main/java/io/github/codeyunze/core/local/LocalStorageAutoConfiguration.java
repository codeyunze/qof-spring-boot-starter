package io.github.codeyunze.core.local;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.spi.FileLifecycleListener;
import io.github.codeyunze.spi.FileMetadataRepository;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 本地存储自动配置。
 *
 * @author 高晗
 * @since 2025/2/16 18:26
 */
@SpringBootConfiguration
@EnableConfigurationProperties({LocalQofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.LOCAL,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class LocalStorageAutoConfiguration {

    /**
     * 本地存储客户端。
     */
    @Bean
    @ConditionalOnMissingBean(LocalQofClient.class)
    public LocalQofClient localQofClient(FileMetadataRepository metadataRepository,
                                         List<FileLifecycleListener> lifecycleListeners) {
        return new LocalQofClient(metadataRepository, lifecycleListeners);
    }
}
