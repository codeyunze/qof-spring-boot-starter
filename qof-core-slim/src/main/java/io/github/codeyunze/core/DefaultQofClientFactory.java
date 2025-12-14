package io.github.codeyunze.core;

import io.github.codeyunze.enums.QofStorageModeEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 高晗
 * @since 2025/2/20 15:19
 */
@Component
public class DefaultQofClientFactory implements QofClientFactory {

    private final ApplicationContext applicationContext;

    public DefaultQofClientFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public QofClient buildClient(String storageMode) {
        if (storageMode == null || storageMode.trim().isEmpty()) {
            throw new IllegalArgumentException("存储模式不能为空");
        }
        
        String mode = storageMode.toLowerCase().trim();

        // 获取所有客户端
        Map<String, QofClient> clients = applicationContext.getBeansOfType(QofClient.class);
        
        if (clients.isEmpty()) {
            throw new IllegalStateException("未找到任何文件存储客户端，请检查配置");
        }

        // 通过类名匹配客户端
        for (Map.Entry<String, QofClient> entry : clients.entrySet()) {
            QofClient client = entry.getValue();
            String className = client.getClass().getSimpleName().toLowerCase();
            String qofClientSuffix = QofClient.class.getSimpleName().toLowerCase();
            
            // 检查类名是否以QofClient结尾
            if (!className.endsWith(qofClientSuffix)) {
                continue;
            }
            
            // 提取存储模式前缀（例如：CosQofClient -> "cos"）
            int suffixIndex = className.lastIndexOf(qofClientSuffix);
            if (suffixIndex > 0) {
                String key = className.substring(0, suffixIndex);
                if (mode.equals(key)) {
                    return client;
                }
            }
        }

        // 提供所有支持的存储模式作为提示
        Set<String> supportedModes = Arrays.stream(QofStorageModeEnum.values())
                .map(QofStorageModeEnum::getMode)
                .collect(Collectors.toSet());

        // 获取已注册的客户端类型（用于调试）
        Set<String> registeredClientTypes = clients.values().stream()
                .map(client -> {
                    String className = client.getClass().getSimpleName();
                    String suffix = QofClient.class.getSimpleName();
                    if (className.endsWith(suffix)) {
                        return className.substring(0, className.length() - suffix.length()).toLowerCase();
                    }
                    return className.toLowerCase();
                })
                .collect(Collectors.toSet());

        if (supportedModes.contains(mode)) {
            throw new IllegalArgumentException(
                    String.format("未启用该存储模式[%s]，配置项为[qof.%s.enable]。已注册的客户端类型: %s", 
                            mode, mode, registeredClientTypes));
        }

        throw new IllegalArgumentException(
                String.format("暂不支持[%s]存储模式。支持的模式: %s，已注册的客户端类型: %s", 
                        storageMode, supportedModes, registeredClientTypes));
    }

}
