package io.github.codeyunze;

import io.github.codeyunze.core.DefaultQofClientFactory;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.metadata.MetadataPersistenceListener;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.ObjectStorageProvider;
import io.github.codeyunze.spi.PersistenceProviderMarker;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 官方 persistence starter 互斥：同时引入多个则启动失败。
     */
    @Bean
    public Object qofPersistenceProviderMutexGuard(List<PersistenceProviderMarker> markers) {
        if (markers != null && markers.size() > 1) {
            String types = markers.stream().map(PersistenceProviderMarker::type).collect(Collectors.joining(", "));
            throw new BeanCreationException(
                    "检测到多个元数据持久化实现：" + types + "，请只保留一个官方 persistence starter。"
            );
        }
        return new Object();
    }

    @Bean
    @ConditionalOnMissingBean(QofClientFactory.class)
    public QofClientFactory qofClientFactory(List<ObjectStorageProvider> providers) {
        return new DefaultQofClientFactory(providers);
    }
}
