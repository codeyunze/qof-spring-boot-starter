package io.github.codeyunze.lifecycle;

import io.github.codeyunze.metadata.FileMetadata;

/**
 * 下载 / 预览生命周期上下文。
 *
 * @author yunze
 * @since 18.0.0
 */
public class DownloadContext {

    private FileMetadata metadata;

    public DownloadContext() {
    }

    public DownloadContext(FileMetadata metadata) {
        this.metadata = metadata;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
}
