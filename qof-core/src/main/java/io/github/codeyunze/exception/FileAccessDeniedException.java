package io.github.codeyunze.exception;

/**
 * 文件访问拒绝异常
 *
 * @author 高晗
 * @since 2025/12/19
 */
public class FileAccessDeniedException extends RuntimeException {

    public FileAccessDeniedException(String message) {
        super(message);
    }

    public FileAccessDeniedException() {
        super("文件访问被拒绝");
    }
}

