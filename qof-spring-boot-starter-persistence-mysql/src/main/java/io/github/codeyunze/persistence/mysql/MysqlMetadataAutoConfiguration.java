package io.github.codeyunze.persistence.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.codeyunze.persistence.mysql.internal.SysFilesMapper;
import io.github.codeyunze.spi.FileMetadataQuery;
import io.github.codeyunze.spi.FileMetadataRepository;
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
@AutoConfigureBefore(name = "io.github.codeyunze.autoconfigure.QofConfiguration")
@ConditionalOnClass(name = "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
@MapperScan("io.github.codeyunze.persistence.mysql.internal")
public class MysqlMetadataAutoConfiguration {

    /**
     * 注册分页插件；未注册时 {@code selectPage} 不会追加 LIMIT，分页不生效。
     * <p>
     * 若宿主已自定义 {@link MybatisPlusInterceptor}，本 Bean 不会覆盖，需自行加入
     * {@link PaginationInnerInterceptor}。
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor qofMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
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
