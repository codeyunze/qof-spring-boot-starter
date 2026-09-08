package io.github.codeyunze.persistence.mysql;

import io.github.codeyunze.QofConfiguration;
import io.github.codeyunze.persistence.mysql.internal.SysFilesMapper;
import io.github.codeyunze.spi.FileMetadataQuery;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.PersistenceProviderMarker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MySQL 元数据持久化自动配置。
 */
@AutoConfiguration
@AutoConfigureBefore(QofConfiguration.class)
@ConditionalOnClass(name = "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
@MapperScan("io.github.codeyunze.persistence.mysql.internal")
public class MysqlMetadataAutoConfiguration {

    @Bean
    public PersistenceProviderMarker mysqlPersistenceProviderMarker() {
        return () -> "mysql";
    }

    @Bean
    @ConditionalOnMissingBean(FileMetadataRepository.class)
    public FileMetadataRepository mysqlFileMetadataRepository(SysFilesMapper mapper) {
        return new MysqlFileMetadataRepository(mapper);
    }

    @Bean
    @ConditionalOnMissingBean(FileMetadataQuery.class)
    public FileMetadataQuery mysqlFileMetadataQuery(SysFilesMapper mapper) {
        return new MysqlFileMetadataQuery(mapper);
    }
}
