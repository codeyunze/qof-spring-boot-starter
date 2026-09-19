package io.github.codeyunze.storage.s3;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.config.QofConstant;
import io.github.codeyunze.lifecycle.FileLifecycleListener;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import jakarta.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * S3 兼容对象存储自动配置。
 *
 * @author 高晗
 * @since 2025/1/12
 */
@ConditionalOnClass(S3Client.class)
@SpringBootConfiguration
@EnableConfigurationProperties({S3QofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.S3,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class S3StorageAutoConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(S3StorageAutoConfiguration.class);

    /**
     * RustFS / MinIO 等 S3 兼容存储通常没有地域概念，
     * 但 AWS SDK 构建客户端时必须提供 region，缺省使用 us-east-1。
     */
    private static final String DEFAULT_REGION = "us-east-1";

    @Resource
    private S3QofProperties s3Properties;

    private Map<String, S3Client> s3ClientMap;

    /**
     * S3 兼容存储客户端。
     */
    @Bean
    @ConditionalOnMissingBean(S3QofClient.class)
    public S3QofClient s3QofClient(FileMetadataRepository metadataRepository,
                                   List<FileLifecycleListener> lifecycleListeners) {
        return new S3QofClient(metadataRepository, lifecycleListeners);
    }

    /**
     * 注册 S3 客户端。
     *
     * @return key 为 storageAlias 客户端的 Bean 名称，value 为客户端
     */
    @Bean
    public Map<String, S3Client> s3ClientMap() {
        this.s3ClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(s3Properties.getMultiple())) {
            S3Client s3Client = createS3Client(s3Properties);
            String key = QofConstant.DEFAULT + StrUtils.toUpperCase(QofConstant.StorageMode.S3);
            this.s3ClientMap.put(key, s3Client);
        } else {
            s3Properties.getMultiple().forEach((storageAlias, config) -> {
                S3Client s3Client = createS3Client(config);
                String key = storageAlias + StrUtils.toUpperCase(QofConstant.StorageMode.S3);
                this.s3ClientMap.put(key, s3Client);
            });
        }
        return this.s3ClientMap;
    }

    /**
     * 根据对应配置信息创建操作客户端。
     *
     * @param config 配置信息
     * @return 客户端
     */
    private S3Client createS3Client(S3QofConfig config) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                config.getAccessKey(),
                config.getSecretKey()
        );

        String region = StringUtils.hasText(config.getRegion()) ? config.getRegion() : DEFAULT_REGION;
        return S3Client.builder()
                .endpointOverride(URI.create(config.getEndpoint()))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .forcePathStyle(true)
                .build();
    }

    /**
     * 应用关闭时，关闭所有 S3 客户端，释放资源。
     */
    @Override
    public void destroy() {
        if (s3ClientMap != null && !s3ClientMap.isEmpty()) {
            log.info("开始关闭S3客户端，共{}个", s3ClientMap.size());
            s3ClientMap.forEach((key, client) -> {
                try {
                    if (client != null) {
                        client.close();
                        log.debug("S3客户端[{}]已关闭", key);
                    }
                } catch (Exception e) {
                    log.error("关闭S3客户端[{}]时发生异常", key, e);
                }
            });
            s3ClientMap.clear();
            log.info("所有S3客户端已关闭");
        }
    }
}
