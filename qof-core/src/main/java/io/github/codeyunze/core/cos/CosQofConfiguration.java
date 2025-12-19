package io.github.codeyunze.core.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
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

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯云COS对象存储客户端操作配置
 *
 * @author 高晗
 * @since 2025/2/16 18:26
 */
@ConditionalOnClass(COSClient.class)    // 当项目中存在COSClient.class类时才会使当前配置类生效
@SpringBootConfiguration
@EnableConfigurationProperties({CosQofProperties.class})
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class CosQofConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(CosQofConfiguration.class);

    @Resource
    private CosQofProperties cosProperties;
    
    private Map<String, COSClient> cosClientMap;

    /**
     * 注册COS客户端
     *
     * @return key为storageAlias客户端的Bean名称，value为客户端
     */
    @Bean
    public Map<String, COSClient> cosClientMap() {
        this.cosClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(cosProperties.getMultiple())) {
            COSClient cosClient = createCosClient(cosProperties);
            // beanName
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
     * 根据对应配置信息创建操作客户端
     *
     * @param config 配置信息
     * @return 客户端
     */
    private COSClient createCosClient(CosQofConfig config) {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }

    /**
     * 应用关闭时，关闭所有COS客户端，释放资源
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
