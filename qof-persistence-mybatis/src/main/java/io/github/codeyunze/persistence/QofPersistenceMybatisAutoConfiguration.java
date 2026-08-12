package io.github.codeyunze.persistence;

import io.github.codeyunze.QofConfiguration;
import io.github.codeyunze.service.FilesService;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.service.impl.AbstractQofServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * MyBatis 元数据持久化自动配置。
 * <p>
 * 先于 {@link QofConfiguration} 装配，以覆盖 core 中的 Noop {@link QofExtService}。
 *
 * @author yunze
 * @since 17.2.0
 */
@AutoConfiguration
@AutoConfigureBefore(QofConfiguration.class)
@ConditionalOnClass(name = "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
@ComponentScan(
        basePackages = "io.github.codeyunze.service.impl",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
@MapperScan("io.github.codeyunze.mapper")
public class QofPersistenceMybatisAutoConfiguration {

    /**
     * 基于 DB 的默认 QofExtService。
     */
    @Bean
    @ConditionalOnMissingBean(QofExtService.class)
    public QofExtService persistentQofExtService(FilesService filesService) {
        return new AbstractQofServiceImpl(filesService) {
        };
    }
}
