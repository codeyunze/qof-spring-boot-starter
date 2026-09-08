package io.github.codeyunze.spi;

/**
 * 官方 persistence starter 标记，用于启动期互斥检测。
 *
 * @author yunze
 * @since 18.0.0
 */
public interface PersistenceProviderMarker {

    /**
     * @return 后端标识，如 {@code mysql}
     */
    String type();
}
