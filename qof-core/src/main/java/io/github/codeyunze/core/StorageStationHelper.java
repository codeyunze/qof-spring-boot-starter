package io.github.codeyunze.core;

import io.github.codeyunze.exception.StorageConfigurationException;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.function.Function;

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
     * @param multiple          多个存储站配置
     * @param defaultStation    默认存储站名称
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
     * @param multiple       多个存储站配置
     * @param configName     配置名称（用于错误提示）
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

    /**
     * 从配置中获取值
     * 如果multiple为空，使用父配置；否则从multiple中获取指定存储站的配置
     *
     * @param fileOperationBase 文件操作基础参数
     * @param multiple          多个存储站配置
     * @param defaultStation    默认存储站名称
     * @param parentValueGetter 从父配置获取值的函数
     * @param configValueGetter 从存储站配置获取值的函数
     * @param configName        配置名称（用于错误提示）
     * @return 配置值
     * @throws StorageConfigurationException 如果找不到存储站配置
     */
    public static <T, R> R getConfigValue(QofFileOperationBase fileOperationBase,
                                          Map<String, T> multiple,
                                          String defaultStation,
                                          Function<Void, R> parentValueGetter,
                                          Function<T, R> configValueGetter,
                                          String configName) {
        String storageStation = getStorageStation(fileOperationBase, multiple, defaultStation);

        // 如果multiple为空，使用父类配置
        if (CollectionUtils.isEmpty(multiple)) {
            return parentValueGetter.apply(null);
        }

        // 从multiple中获取配置
        T config = getStorageConfig(storageStation, multiple, configName);
        R value = configValueGetter.apply(config);

        // 如果存储站配置中没有值，尝试使用父配置
        if (value == null) {
            return parentValueGetter.apply(null);
        }

        return value;
    }
}

