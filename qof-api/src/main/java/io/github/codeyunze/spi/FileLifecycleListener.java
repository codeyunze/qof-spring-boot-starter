package io.github.codeyunze.spi;

/**
 * 文件生命周期监听器（可组合，支持多个实现按 Order 执行）。
 * <p>
 * 用于替代「{@code @Primary} 整类覆盖 QofExtService」的扩展方式；
 * 鉴权、审计、持久化等应拆成独立 Listener。
 *
 * @author yunze
 * @since 17.1.0
 */
public interface FileLifecycleListener {

    /**
     * 上传前。
     *
     * @param context 上传上下文（当前为松散 Object，后续版本将收紧为强类型）
     */
    default void beforeUpload(Object context) {
    }

    /**
     * 上传后。
     *
     * @param context 上传上下文
     */
    default void afterUpload(Object context) {
    }

    /**
     * 下载前。
     *
     * @param context 下载上下文
     */
    default void beforeDownload(Object context) {
    }

    /**
     * 下载后。
     *
     * @param context 下载上下文
     */
    default void afterDownload(Object context) {
    }

    /**
     * 删除前。
     *
     * @param context 删除上下文
     * @return {@code false} 表示中断删除
     */
    default boolean beforeDelete(Object context) {
        return true;
    }

    /**
     * 删除后。
     *
     * @param context 删除上下文
     * @param deleted 存储侧是否删除成功
     * @return 扩展处理是否成功
     */
    default boolean afterDelete(Object context, boolean deleted) {
        return true;
    }
}
