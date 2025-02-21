package io.github.codeyunze.core;

import io.github.codeyunze.enums.QofStorageModeEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yunze
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
        String mode = storageMode.toLowerCase();

        // 获取所有客户端
        Map<String, QofClient> clients = applicationContext.getBeansOfType(QofClient.class);

        for (Map.Entry<String, QofClient> entry : clients.entrySet()) {
            String clientBeanName = entry.getValue().getClass().getSimpleName().toLowerCase();
            String key = clientBeanName.substring(0, clientBeanName.lastIndexOf(QofClient.class.getSimpleName().toLowerCase()));
            if (mode.equals(key)) {
                return entry.getValue();
            }
        }

        // 提供所有支持的存储模式作为提示
        Set<String> supportedModes = Arrays.stream(QofStorageModeEnum.values())
                .map(QofStorageModeEnum::getMode)
                .collect(Collectors.toSet());
        throw new IllegalArgumentException("暂不支持[" + storageMode + "]存储模式，支持的模式为: " + supportedModes);
    }

}
