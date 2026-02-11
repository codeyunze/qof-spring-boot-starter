package io.github.codeyunze.core.rustfs;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import jakarta.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * RustFS对象存储客户端操作配置
 *
 * @author 高晗
 * @since 2025/1/12
 */
@ConditionalOnClass(S3Client.class)    // 当项目中存在S3Client.class类时才会使当前配置类生效
@SpringBootConfiguration
@EnableConfigurationProperties({RustfsQofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.RUSTFS,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class RustfsQofConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(RustfsQofConfiguration.class);

    @Resource
    private RustfsQofProperties rustfsProperties;
    
    private Map<String, S3Client> s3ClientMap;

    /**
     * 注册S3客户端
     *
     * @return key为storageAlias客户端的Bean名称，value为客户端
     */
    @Bean
    public Map<String, S3Client> s3ClientMap() {
        this.s3ClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(rustfsProperties.getMultiple())) {
            S3Client s3Client = createS3Client(rustfsProperties);
            // beanName
            String key = QofConstant.DEFAULT + StrUtils.toUpperCase(QofConstant.StorageMode.RUSTFS);
            this.s3ClientMap.put(key, s3Client);
        } else {
            rustfsProperties.getMultiple().forEach((storageAlias, config) -> {
                S3Client s3Client = createS3Client(config);
                String key = storageAlias + StrUtils.toUpperCase(QofConstant.StorageMode.RUSTFS);
                this.s3ClientMap.put(key, s3Client);
            });
        }
        return this.s3ClientMap;
    }

    /**
     * 根据对应配置信息创建操作客户端
     *
     * @param config 配置信息
     * @return 客户端
     */
    private S3Client createS3Client(RustfsQofConfig config) {
        // 创建AWS凭证
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                config.getAccessKey(),
                config.getSecretKey()
        );

        // 构建S3客户端
        return S3Client.builder()
                .endpointOverride(URI.create(config.getEndpoint()))
                .region(Region.of(config.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .forcePathStyle(true)   // RustFS需要启用Path-style
                .build();
    }

    /**
     * 应用关闭时，关闭所有S3客户端，释放资源
     */
    @Override
    public void destroy() {
        if (s3ClientMap != null && !s3ClientMap.isEmpty()) {
            log.info("开始关闭RustFS S3客户端，共{}个", s3ClientMap.size());
            s3ClientMap.forEach((key, client) -> {
                try {
                    if (client != null) {
                        client.close();
                        log.debug("RustFS S3客户端[{}]已关闭", key);
                    }
                } catch (Exception e) {
                    log.error("关闭RustFS S3客户端[{}]时发生异常", key, e);
                }
            });
            s3ClientMap.clear();
            log.info("所有RustFS S3客户端已关闭");
        }
    }
}
