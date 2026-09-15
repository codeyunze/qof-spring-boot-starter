package io.github.codeyunze.autoconfigure;

import io.github.codeyunze.config.QofProperties;
import io.github.codeyunze.core.DefaultQofClientFactory;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.metadata.MetadataPersistenceListener;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.provider.ObjectStorageProvider;
import org.springframework.beans.factory.BeanCreationException;
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
 * <p>
 * 放在 {@code autoconfigure} 子包，避免与业务 / examples 共用根包
 * {@code io.github.codeyunze} 时 IDE 多模块增量编译互相覆盖 class。
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
     * 无 {@link FileMetadataRepository} 时启动失败（禁止 Noop 不落库模式）。
     */
    @Bean
    @ConditionalOnMissingBean(FileMetadataRepository.class)
    public FileMetadataRepository missingFileMetadataRepository() {
        throw new BeanCreationException(
                "未找到 FileMetadataRepository。"
                        + "请引入 qof-spring-boot-starter-persistence-mysql，或自行实现并注册 FileMetadataRepository Bean。"
        );
    }

    @Bean
    @ConditionalOnMissingBean(MetadataPersistenceListener.class)
    public MetadataPersistenceListener metadataPersistenceListener(FileMetadataRepository repository) {
        return new MetadataPersistenceListener(repository);
    }

    @Bean
    @ConditionalOnMissingBean(QofClientFactory.class)
    public QofClientFactory qofClientFactory(List<ObjectStorageProvider> providers) {
        return new DefaultQofClientFactory(providers);
    }
}
