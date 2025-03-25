package io.github.codeyunze.core.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.utils.StrUtils;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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
// 只有当qof.cos.enable=true时，QofCosConfiguration配置类才会被加载
// @ConditionalOnProperty(
//         prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS,
//         name = QofConstant.ENABLE,
//         havingValue = QofConstant.ENABLE_VALUE)
public class CosQofConfiguration {

    @Resource
    private CosQofProperties cosProperties;

    /**
     * 注册COS客户端
     *
     * @return key为storageAlias客户端的Bean名称，value为客户端
     */
    @Bean
    public Map<String, COSClient> cosClientMap() {
        Map<String, COSClient> cosClientMap = new HashMap<>();
        if (CollectionUtils.isEmpty(cosProperties.getMultiple())) {
            COSClient cosClient = createCosClient(cosProperties);
            // beanName
            String key = QofConstant.DEFAULT + StrUtils.toUpperCase(QofConstant.StorageMode.COS);
            cosClientMap.put(key, cosClient);
        } else {
            cosProperties.getMultiple().forEach((storageAlias, config) -> {
                COSClient cosClient = createCosClient(config);
                String key = storageAlias + StrUtils.toUpperCase(QofConstant.StorageMode.COS);
                cosClientMap.put(key, cosClient);
            });
        }
        return cosClientMap;
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
}
