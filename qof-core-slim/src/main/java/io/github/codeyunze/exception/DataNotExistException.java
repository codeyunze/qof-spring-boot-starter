package io.github.codeyunze.exception;

/**
 * 业务数据不存在异常
 *
 * @author yunze
 * @since 2024/11/23 星期六 14:45
 */
public class DataNotExistException extends RuntimeException {

    public DataNotExistException(String message) {
        super(message);
    }

    public DataNotExistException() {
        super("数据不存在");
    }
}
