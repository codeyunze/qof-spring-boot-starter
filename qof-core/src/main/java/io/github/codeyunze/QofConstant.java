package io.github.codeyunze;

/**
 * 常量信息
 *
 * @author 高晗
 * @since 2025/2/16 18:04
 */
public class QofConstant {

    /**
     * 项目简称 QOF (Quickly operate files)
     */
    public static final String QOF = "qof";

    /**
     * 启动属性配置名
     */
    public static final String ENABLE = "enable";

    /**
     * 启动属性配置所需值
     */
    public static final String ENABLE_VALUE = "true";

    /**
     * 默认客户端
     */
    public static final String DEFAULT = "default";

    /**
     * 客户端
     */
    public static final String CLIENT = "client";

    /**
     * 文件存储类型
     */
    public interface StorageMode {
        String LOCAL = "local";
        String COS = "cos";
        String OSS = "oss";
        String minio = "minio";
    }
}
