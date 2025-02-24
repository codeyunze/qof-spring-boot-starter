package io.github.codeyunze.exception;

/**
 * 不支持操作文件类型异常
 * @author yunze
 * @since 2025/2/24 14:10
 */
public class TypeNotSupportedException extends RuntimeException {

    public TypeNotSupportedException(String message) {
        super(message);
    }

    public TypeNotSupportedException() {
        super("不支持该附件类型");
    }
}
