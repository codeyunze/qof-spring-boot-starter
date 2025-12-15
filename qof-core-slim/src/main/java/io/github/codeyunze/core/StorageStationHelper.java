package io.github.codeyunze.core;

import io.github.codeyunze.exception.StorageConfigurationException;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 存储站配置辅助工具类
 * 用于统一处理存储站配置的获取逻辑，减少代码重复
 *
 * @author 高晗
 * @since 2025/2/25
 */
public class StorageStationHelper {

    /**
     * 获取存储站名称
     * 如果multiple为空或不存在指定的存储站，则返回默认存储站
     *
     * @param fileOperationBase 文件操作基础参数
     * @param multiple           多个存储站配置
     * @param defaultStation     默认存储站名称
     * @return 存储站名称
     */
    public static <T> String getStorageStation(QofFileOperationBase fileOperationBase,
                                                Map<String, T> multiple,
                                                String defaultStation) {
        if (CollectionUtils.isEmpty(multiple) || !multiple.containsKey(fileOperationBase.getFileStorageStation())) {
            return defaultStation;
        }
        return fileOperationBase.getFileStorageStation();
    }

    /**
     * 获取存储站配置对象
     *
     * @param storageStation 存储站名称
     * @param multiple      多个存储站配置
     * @param configName    配置名称（用于错误提示）
     * @return 存储站配置对象
     * @throws StorageConfigurationException 如果找不到存储站配置
     */
    public static <T> T getStorageConfig(String storageStation,
                                         Map<String, T> multiple,
                                         String configName) {
        if (CollectionUtils.isEmpty(multiple)) {
            return null;
        }

        T config = multiple.get(storageStation);
        if (config == null) {
            throw new StorageConfigurationException("未找到存储站配置[" + configName + "]: " + storageStation);
        }

        return config;
    }
}

