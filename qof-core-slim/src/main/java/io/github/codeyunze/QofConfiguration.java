package io.github.codeyunze;

import org.mybatis.spring.annotation.MapperScan;
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
}
