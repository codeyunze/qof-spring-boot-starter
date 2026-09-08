package io.github.codeyunze.spi;

import io.github.codeyunze.spi.lifecycle.DeleteContext;
import io.github.codeyunze.spi.lifecycle.DownloadContext;
import io.github.codeyunze.spi.lifecycle.UploadContext;

/**
 * 文件生命周期监听器（可组合，支持多个实现按 {@code @Order} 执行）。
 * <p>
 * 用于鉴权、审计、元数据落库等横切逻辑；官方落库由 {@code MetadataPersistenceListener} 实现。
 *
 * @author yunze
 * @since 17.1.0
 */
public interface FileLifecycleListener {

    /**
     * 上传前钩子。
     * <p>
     * 在对象存储实际上传之前调用；可用于参数校验、配额检查等。
     * 默认空实现。
     *
     * @param context 上传上下文（含待落库 / 待上传的元数据）
     */
    default void beforeUpload(UploadContext context) {
    }

    /**
     * 上传后钩子。
     * <p>
     * 在对象存储上传成功之后调用；官方 {@code MetadataPersistenceListener} 在此写入元数据。
     * 默认空实现。
     *
     * @param context 上传上下文（含上传完成后的元数据，含 fileId、路径等）
     */
    default void afterUpload(UploadContext context) {
    }

    /**
     * 下载前钩子。
     * <p>
     * 在按 fileId 查到元数据、真正拉取对象流之前调用；可用于额外鉴权、审计埋点等。
     * 默认空实现。
     *
     * @param context 下载上下文（含已查到的文件元数据）
     */
    default void beforeDownload(DownloadContext context) {
    }

    /**
     * 下载后钩子。
     * <p>
     * 在对象流获取完成之后调用；可用于下载计数、审计等。
     * 默认空实现。
     *
     * @param context 下载上下文（含文件元数据）
     */
    default void afterDownload(DownloadContext context) {
    }

    /**
     * 删除前钩子。
     * <p>
     * 在删除对象存储文件之前调用。约定：官方落库 Listener 在此<strong>先删元数据</strong>；
     * 任一 Listener 返回 {@code false} 则中止后续删对象，整次删除失败。
     *
     * @param context 删除上下文（含待删文件元数据）
     * @return {@code true} 继续删除；{@code false} 中断删除（例如元数据删除失败）
     */
    default boolean beforeDelete(DeleteContext context) {
        return true;
    }

    /**
     * 删除后钩子。
     * <p>
     * 在对象存储删除尝试完成之后调用（无论对象侧成功与否）。
     * 对象删除失败时 Client 仅记日志、不回滚已删元数据；本钩子可用于补偿登记等。
     *
     * @param context       删除上下文（含文件元数据）
     * @param objectDeleted 对象存储侧是否删除成功
     * @return 扩展处理是否成功；当前 Client 不据此回滚，仅供业务自用
     */
    default boolean afterDelete(DeleteContext context, boolean objectDeleted) {
        return true;
    }
}
