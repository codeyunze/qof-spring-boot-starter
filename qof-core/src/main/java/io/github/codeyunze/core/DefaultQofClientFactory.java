package io.github.codeyunze.core;

import io.github.codeyunze.enums.QofStorageModeEnum;
import io.github.codeyunze.spi.ObjectStorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 {@link ObjectStorageProvider} 显式注册的客户端工厂。
 * <p>
 * 不再按类名猜测存储模式；第三方扩展只需实现 Provider 并注册为 Bean。
 *
 * @author 高晗
 * @since 2025/2/20 15:19
 */
public class DefaultQofClientFactory implements QofClientFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultQofClientFactory.class);

    private final Map<String, QofClient> clientsByMode;

    /**
     * @param providers 已注册的存储提供者
     */
    public DefaultQofClientFactory(List<ObjectStorageProvider> providers) {
        Map<String, QofClient> map = new LinkedHashMap<>();
        if (providers != null) {
            for (ObjectStorageProvider provider : providers) {
                if (provider == null || provider.mode() == null || provider.mode().isBlank()) {
                    continue;
                }
                String mode = provider.mode().trim().toLowerCase(Locale.ROOT);
                Object clientObj = provider.getClient();
                if (!(clientObj instanceof QofClient client)) {
                    throw new IllegalStateException("ObjectStorageProvider[" + mode + "] 未返回 QofClient 实例");
                }
                if (map.containsKey(mode)) {
                    throw new IllegalStateException("存储模式重复注册: " + mode);
                }
                map.put(mode, client);
            }
        }
        this.clientsByMode = Collections.unmodifiableMap(map);
        log.info("QOF ClientFactory ready, modes={}", clientsByMode.keySet());
    }

    @Override
    public QofClient buildClient(String storageMode) {
        if (storageMode == null || storageMode.trim().isEmpty()) {
            throw new IllegalArgumentException("存储模式不能为空");
        }
        String mode = storageMode.toLowerCase(Locale.ROOT).trim();
        if (clientsByMode.isEmpty()) {
            throw new IllegalStateException("未找到任何文件存储客户端，请检查 qof.<mode>.enable 配置及对应依赖");
        }
        QofClient client = clientsByMode.get(mode);
        if (client != null) {
            return client;
        }
        Set<String> supportedModes = Arrays.stream(QofStorageModeEnum.values())
                .map(QofStorageModeEnum::getMode)
                .collect(Collectors.toSet());
        Collection<String> registered = clientsByMode.keySet();
        if (supportedModes.contains(mode)) {
            throw new IllegalArgumentException(String.format(
                    "未启用该存储模式[%s]，配置项为[qof.%s.enable=true]，并确保对应 SDK 依赖在 classpath。已注册: %s",
                    mode, mode, registered));
        }
        throw new IllegalArgumentException(String.format(
                "暂不支持[%s]存储模式。内置模式: %s，已注册: %s", storageMode, supportedModes, registered));
    }
}
