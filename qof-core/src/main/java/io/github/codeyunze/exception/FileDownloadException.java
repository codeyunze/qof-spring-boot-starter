package io.github.codeyunze.exception;

/**
 * 文件下载异常
 *
 * @author 高晗
 * @since 2025/2/18
 */
public class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message) {
        super(message);
    }

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

