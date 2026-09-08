package io.github.codeyunze.spi.lifecycle;

import io.github.codeyunze.spi.metadata.FileMetadata;

/**
 * 删除生命周期上下文。
 *
 * @author yunze
 * @since 18.0.0
 */
public class DeleteContext {

    private FileMetadata metadata;

    public DeleteContext() {
    }

    public DeleteContext(FileMetadata metadata) {
        this.metadata = metadata;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
}
