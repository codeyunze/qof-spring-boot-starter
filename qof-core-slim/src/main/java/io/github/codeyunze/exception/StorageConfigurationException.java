package io.github.codeyunze.exception;

/**
 * 存储配置异常
 *
 * @author 高晗
 * @since 2025/2/18
 */
public class StorageConfigurationException extends RuntimeException {

    public StorageConfigurationException(String message) {
        super(message);
    }

    public StorageConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

