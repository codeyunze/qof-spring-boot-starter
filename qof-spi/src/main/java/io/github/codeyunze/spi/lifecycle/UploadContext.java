package io.github.codeyunze.spi.lifecycle;

import io.github.codeyunze.spi.metadata.FileMetadata;

/**
 * 上传生命周期上下文。
 *
 * @author yunze
 * @since 18.0.0
 */
public class UploadContext {

    private FileMetadata metadata;

    public UploadContext() {
    }

    public UploadContext(FileMetadata metadata) {
        this.metadata = metadata;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
}
