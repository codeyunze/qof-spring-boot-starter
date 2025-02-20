package io.github.codeyunze.core;

import io.github.codeyunze.enums.QofStorageModeEnum;

/**
 * 客户端构建工程
 * @author yunze
 * @since 2025/2/19 23:18
 */
public interface QofClientFactory {

    /**
     * 构建客户端
     *
     * @param storageMode 存储模式 {@link QofStorageModeEnum#getMode()}
     * @return 存储模式所对应的客户端
     */
    QofClient buildClient(String storageMode);
}
