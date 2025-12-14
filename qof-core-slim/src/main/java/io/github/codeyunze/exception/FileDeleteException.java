package io.github.codeyunze.exception;

/**
 * 文件删除异常
 *
 * @author 高晗
 * @since 2025/2/18
 */
public class FileDeleteException extends RuntimeException {

    public FileDeleteException(String message) {
        super(message);
    }

    public FileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}

