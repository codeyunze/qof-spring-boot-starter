package io.github.codeyunze;

import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.service.SysFilesService;
import io.github.codeyunze.service.impl.AbstractQofServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 高晗
 * @since 2024/6/23 星期日 17:18
 */
@Configuration
@ComponentScan({"io.github.codeyunze"})
@MapperScan({"io.github.codeyunze.mapper"})
public class QofConfiguration {

    /**
     * 提供默认的QofExtService实现
     * 当用户没有提供自定义实现时使用此默认实现
     */
    @Bean
    @ConditionalOnMissingBean(QofExtService.class)
    public QofExtService defaultQofExtService(SysFilesService sysFilesService) {
        return new AbstractQofServiceImpl(sysFilesService) {
            // 使用匿名内部类实现抽象类
        };
    }
}
