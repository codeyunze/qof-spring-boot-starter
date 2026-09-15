package io.github.codeyunze.persistence.mongo;

import io.github.codeyunze.persistence.mongo.internal.SysFilesDocument;
import io.github.codeyunze.spi.FileMetadataQuery;
import io.github.codeyunze.spi.FileMetadataRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * MongoDB 元数据持久化自动配置。
 * <p>
 * 与 MySQL starter 互斥：请只引入其中一个官方 persistence starter。
 * 宿主需自行配置 {@code spring.data.mongodb.uri}（或等价项）。
 */
@AutoConfiguration
@AutoConfigureBefore(name = "io.github.codeyunze.autoconfigure.QofConfiguration")
@ConditionalOnClass(MongoTemplate.class)
public class MongoMetadataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FileMetadataRepository.class)
    public FileMetadataRepository mongoFileMetadataRepository(MongoTemplate mongoTemplate) {
        ensureIndexes(mongoTemplate);
        return new MongoFileMetadataRepository(mongoTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(FileMetadataQuery.class)
    public FileMetadataQuery mongoFileMetadataQuery(MongoTemplate mongoTemplate) {
        return new MongoFileMetadataQuery(mongoTemplate);
    }

    private static void ensureIndexes(MongoTemplate mongoTemplate) {
        // 与文档注解双保险：启动时显式创建常用索引
        mongoTemplate.indexOps(SysFilesDocument.class)
                .createIndex(new Index().on("invalid", Sort.Direction.ASC).on("create_time", Sort.Direction.DESC));
        mongoTemplate.indexOps(SysFilesDocument.class)
                .createIndex(new Index().on("file_storage_mode", Sort.Direction.ASC)
                        .on("file_storage_station", Sort.Direction.ASC));
        mongoTemplate.indexOps(SysFilesDocument.class)
                .createIndex(new Index().on("file_name", Sort.Direction.ASC));
    }
}
