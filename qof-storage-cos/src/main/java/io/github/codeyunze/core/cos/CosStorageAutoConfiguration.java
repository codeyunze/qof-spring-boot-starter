package io.github.codeyunze.core.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.spi.FileLifecycleListener;
import io.github.codeyunze.spi.FileMetadataRepository;
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

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云 COS 对象存储自动配置。
 *
 * @author 高晗
 * @since 2025/2/16 18:26
 */
@ConditionalOnClass(COSClient.class)
@SpringBootConfiguration
@EnableConfigurationProperties({CosQofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class CosStorageAutoConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(CosStorageAutoConfiguration.class);

    @Resource
    private CosQofProperties cosProperties;

    private Map<String, COSClient> cosClientMap;

    /**
     * COS 存储客户端。
     */
    @Bean
    @ConditionalOnMissingBean(CosQofClient.class)
    public CosQofClient cosQofClient(FileMetadataRepository metadataRepository,
                                     List<FileLifecycleListener> lifecycleListeners) {
        return new CosQofClient(metadataRepository, lifecycleListeners);
    }

    /**
     * 注册 COS 客户端。
     *
     * @return key 为 storageAlias 客户端的 Bean 名称，value 为客户端
     */
    @Bean
    public Map<String, COSClient> cosClientMap() {
        this.cosClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(cosProperties.getMultiple())) {
            COSClient cosClient = createCosClient(cosProperties);
            String key = QofConstant.DEFAULT + StrUtils.toUpperCase(QofConstant.StorageMode.COS);
            this.cosClientMap.put(key, cosClient);
        } else {
            cosProperties.getMultiple().forEach((storageAlias, config) -> {
                COSClient cosClient = createCosClient(config);
                String key = storageAlias + StrUtils.toUpperCase(QofConstant.StorageMode.COS);
                this.cosClientMap.put(key, cosClient);
            });
        }
        return this.cosClientMap;
    }

    /**
     * 根据对应配置信息创建操作客户端。
     *
     * @param config 配置信息
     * @return 客户端
     */
    private COSClient createCosClient(CosQofConfig config) {
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        return new COSClient(cred, clientConfig);
    }

    /**
     * 应用关闭时，关闭所有 COS 客户端，释放资源。
     */
    @Override
    public void destroy() {
        if (cosClientMap != null && !cosClientMap.isEmpty()) {
            log.info("开始关闭COS客户端，共{}个", cosClientMap.size());
            cosClientMap.forEach((key, client) -> {
                try {
                    if (client != null) {
                        client.shutdown();
                        log.debug("COS客户端[{}]已关闭", key);
                    }
                } catch (Exception e) {
                    log.error("关闭COS客户端[{}]时发生异常", key, e);
                }
            });
            cosClientMap.clear();
            log.info("所有 COS 客户端已关闭");
        }
    }
}
