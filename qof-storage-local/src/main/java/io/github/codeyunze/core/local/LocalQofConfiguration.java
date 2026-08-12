package io.github.codeyunze.core.local;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.service.QofExtService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 本地存储操作配置。
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
public class LocalQofConfiguration {

    /**
     * 本地存储客户端。
     */
    @Bean
    @ConditionalOnMissingBean(LocalQofClient.class)
    public LocalQofClient localQofClient(QofExtService qofExtService) {
        return new LocalQofClient(qofExtService);
    }
}
