package io.github.codeyunze.core.local;

/**
 * 本地文件存储的属性配置信息
 *
 * @author 高晗
 * @since 2025/2/16 20:39
 */
public class LocalQofConfig {

    /**
     * 文件存储路径
     */
    private String filepath;

    /**
     * 文件预览地址
     */
    private String previewAddress;

    public String getPreviewAddress() {
        return previewAddress;
    }

    public void setPreviewAddress(String previewAddress) {
        this.previewAddress = previewAddress;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
