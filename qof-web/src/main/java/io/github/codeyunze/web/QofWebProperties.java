package io.github.codeyunze.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QOF Web 层配置。
 *
 * @author yunze
 * @since 17.1.0
 */
@ConfigurationProperties(prefix = "qof.web")
public class QofWebProperties {

    /**
     * 是否启用内置 FileController，默认 false，避免抢占宿主路由。
     */
    private boolean enabled = false;

    /**
     * Controller 路径前缀。
     */
    private String basePath = "/file";

    /**
     * 是否注册异常 Advice（仅处理 QOF 包内控制器相关异常）。
     */
    private boolean exposeAdvice = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isExposeAdvice() {
        return exposeAdvice;
    }

    public void setExposeAdvice(boolean exposeAdvice) {
        this.exposeAdvice = exposeAdvice;
    }
}
