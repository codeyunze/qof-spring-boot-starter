package io.github.codeyunze;

import io.github.codeyunze.controller.FileController;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.exception.QofOverallExceptionHandle;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.service.impl.FileValidationServiceImpl;
import io.github.codeyunze.web.QofWebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * QOF Web 自动配置：默认不启用内置 Controller / Advice。
 *
 * @author 高晗
 * @since 2024/6/23 星期日 17:18
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(QofWebProperties.class)
public class QofCoreConfiguration {

    /**
     * Multipart 适配服务。
     */
    @Bean
    @ConditionalOnMissingBean(FileValidationService.class)
    public FileValidationService fileValidationService() {
        return new FileValidationServiceImpl();
    }

    /**
     * 内置文件 HTTP API（{@code qof.web.enabled=true} 时注册）。
     */
    @Bean
    @ConditionalOnProperty(prefix = "qof.web", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(FileController.class)
    public FileController fileController(QofClientFactory qofClientFactory) {
        return new FileController(qofClientFactory);
    }

    /**
     * QOF 包边界异常处理（{@code qof.web.expose-advice=true} 时注册）。
     */
    @Bean
    @ConditionalOnProperty(prefix = "qof.web", name = "expose-advice", havingValue = "true")
    @ConditionalOnMissingBean(QofOverallExceptionHandle.class)
    public QofOverallExceptionHandle qofOverallExceptionHandle() {
        return new QofOverallExceptionHandle();
    }
}
