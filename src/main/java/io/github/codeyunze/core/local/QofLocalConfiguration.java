package io.github.codeyunze.core.local;

import cn.hutool.core.text.CharPool;
import io.github.codeyunze.QofConstant;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 本地存储操作配置
 *
 * @author yunze
 * @since 2025/2/16 18:26
 */
@SpringBootConfiguration
@EnableConfigurationProperties({QofLocalProperties.class})
// 只有当qof.cos.enable=true时，QofLocalConfiguration配置类才会被加载
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.LOCAL,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class QofLocalConfiguration {

}
