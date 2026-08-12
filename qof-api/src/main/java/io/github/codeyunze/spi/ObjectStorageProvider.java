package io.github.codeyunze.spi;

/**
 * 对象存储提供者 SPI。
 * <p>
 * 第三方扩展自定义存储时实现本接口并注册为 Spring Bean，
 * 由 {@code QofClientFactory} 按 {@link #mode()} 显式匹配，禁止依赖类名猜测。
 *
 * @author yunze
 * @since 17.1.0
 */
public interface ObjectStorageProvider {

    /**
     * 稳定的存储模式标识，如 {@code local} / {@code cos} / {@code oss} / {@code rustfs}。
     *
     * @return 模式标识（小写）
     */
    String mode();

    /**
     * 获取可执行文件操作的客户端实例。
     *
     * @return 客户端；通常返回实现类自身
     */
    Object getClient();
}
