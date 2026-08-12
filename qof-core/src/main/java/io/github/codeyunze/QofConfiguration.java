package io.github.codeyunze;

import io.github.codeyunze.core.DefaultQofClientFactory;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.service.impl.NoopQofExtService;
import io.github.codeyunze.spi.ObjectStorageProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.util.List;

/**
 * QOF 核心自动配置（不含具体存储与持久化，由对应模块自行装配）。
 *
 * @author 高晗
 * @since 2024/6/23 星期日 17:18
 */
@AutoConfiguration
@EnableConfigurationProperties(QofProperties.class)
@ComponentScan(
        basePackages = {
                "io.github.codeyunze.core.validation"
        },
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class QofConfiguration {

    /**
     * 无持久化模块时的默认扩展（Noop）。
     */
    @Bean
    @ConditionalOnMissingBean(QofExtService.class)
    public QofExtService defaultQofExtService() {
        return new NoopQofExtService();
    }

    /**
     * 基于 {@link ObjectStorageProvider} 的客户端工厂。
     */
    @Bean
    @ConditionalOnMissingBean(QofClientFactory.class)
    public QofClientFactory qofClientFactory(List<ObjectStorageProvider> providers) {
        return new DefaultQofClientFactory(providers);
    }
}
