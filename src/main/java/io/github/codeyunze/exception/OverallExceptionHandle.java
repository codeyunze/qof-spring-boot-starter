package io.github.codeyunze.exception;


import io.github.codeyunze.utils.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常捕获
 *
 * @author yunze
 * @since 2024/6/19 星期三 22:57
 */
@RestControllerAdvice
public class OverallExceptionHandle {

    /**
     * 参数校验异常提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        return new Result<>(1, null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 数据不存在异常问题处理
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> dataNotExistExceptionHandle(DataNotExistException e) {
        return new Result<>(2, null, e.getMessage());
    }


    /**
     * 不支持操作文件类型异常问题处理
     */
    @ExceptionHandler(TypeNotSupportedException.class)
    Result<?> typeNotSupportedExceptionHandle(TypeNotSupportedException e) {
        return new Result<>(3, null, e.getMessage());
    }
}
