package io.github.codeyunze.exception;

/**
 * 文件上传异常
 *
 * @author 高晗
 * @since 2025/2/18
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}

