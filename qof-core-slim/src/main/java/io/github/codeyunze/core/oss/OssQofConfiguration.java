package io.github.codeyunze.core.oss;

import cn.hutool.core.text.CharPool;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
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

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OSS对象存储客户端操作配置
 *
 * @author 高晗
 * @since 2025/2/18
 */
@ConditionalOnClass(OSS.class)    // 当项目中存在OSS.class类时才会使当前配置类生效
@SpringBootConfiguration
@EnableConfigurationProperties({OssQofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.OSS,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class OssQofConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(OssQofConfiguration.class);

    @Resource
    private OssQofProperties ossProperties;
    
    private Map<String, OSS> ossClientMap;

    /**
     * 注册OSS客户端
     *
     * @return key为storageAlias客户端的Bean名称，value为客户端
     */
    @Bean
    public Map<String, OSS> ossClientMap() {
        this.ossClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(ossProperties.getMultiple())) {
            OSS ossClient = createOssClient(ossProperties);
            // beanName
            String key = QofConstant.DEFAULT + StrUtils.toUpperCase(QofConstant.StorageMode.OSS);
            this.ossClientMap.put(key, ossClient);
        } else {
            ossProperties.getMultiple().forEach((storageAlias, config) -> {
                OSS ossClient = createOssClient(config);
                String key = storageAlias + StrUtils.toUpperCase(QofConstant.StorageMode.OSS);
                this.ossClientMap.put(key, ossClient);
            });
        }
        return this.ossClientMap;
    }

    /**
     * 根据对应配置信息创建操作客户端
     *
     * @param config 配置信息
     * @return 客户端
     */
    private OSS createOssClient(OssQofConfig config) {
        // 创建OSS客户端实例
        return new OSSClientBuilder().build(
                config.getEndpoint(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
        );
    }

    /**
     * 应用关闭时，关闭所有OSS客户端，释放资源
     */
    @Override
    public void destroy() {
        if (ossClientMap != null && !ossClientMap.isEmpty()) {
            log.info("开始关闭OSS客户端，共{}个", ossClientMap.size());
            ossClientMap.forEach((key, client) -> {
                try {
                    if (client != null) {
                        client.shutdown();
                        log.debug("OSS客户端[{}]已关闭", key);
                    }
                } catch (Exception e) {
                    log.error("关闭OSS客户端[{}]时发生异常", key, e);
                }
            });
            ossClientMap.clear();
            log.info("所有OSS客户端已关闭");
        }
    }
}

