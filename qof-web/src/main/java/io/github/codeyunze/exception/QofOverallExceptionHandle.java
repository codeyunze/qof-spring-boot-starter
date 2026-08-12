package io.github.codeyunze.exception;

import io.github.codeyunze.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.Objects;

/**
 * QOF Web 异常捕获（仅作用于 {@code io.github.codeyunze.controller}，不污染宿主全局异常）。
 *
 * @author 高晗
 * @since 2024/6/19 星期三 22:57
 */
@RestControllerAdvice(basePackages = "io.github.codeyunze.controller")
public class QofOverallExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(QofOverallExceptionHandle.class);

    /**
     * 参数校验异常提示。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        return new Result<>(1, null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 数据不存在。
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> dataNotExistExceptionHandle(DataNotExistException e) {
        return new Result<>(2, null, e.getMessage());
    }

    /**
     * 不支持的文件类型。
     */
    @ExceptionHandler(TypeNotSupportedException.class)
    Result<?> typeNotSupportedExceptionHandle(TypeNotSupportedException e) {
        return new Result<>(3, null, e.getMessage());
    }

    /**
     * 非法参数。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    Result<?> illegalArgumentExceptionHandle(IllegalArgumentException e) {
        return new Result<>(4, null, e.getMessage());
    }

    /**
     * 文件上传异常。
     */
    @ExceptionHandler(FileUploadException.class)
    Result<?> fileUploadExceptionHandle(FileUploadException e) {
        log.error("文件上传异常", e);
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败: " + e.getMessage());
    }

    /**
     * 文件下载异常。
     */
    @ExceptionHandler(FileDownloadException.class)
    Result<?> fileDownloadExceptionHandle(FileDownloadException e) {
        log.error("文件下载异常", e);
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件下载失败: " + e.getMessage());
    }

    /**
     * 文件删除异常。
     */
    @ExceptionHandler(FileDeleteException.class)
    Result<?> fileDeleteExceptionHandle(FileDeleteException e) {
        log.error("文件删除异常", e);
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件删除失败: " + e.getMessage());
    }

    /**
     * 存储配置异常。
     */
    @ExceptionHandler(StorageConfigurationException.class)
    Result<?> storageConfigurationExceptionHandle(StorageConfigurationException e) {
        log.error("存储配置异常", e);
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "存储配置错误: " + e.getMessage());
    }

    /**
     * 文件访问拒绝。
     */
    @ExceptionHandler(FileAccessDeniedException.class)
    Result<?> fileAccessDeniedExceptionHandle(FileAccessDeniedException e) {
        log.warn("文件访问被拒绝: {}", e.getMessage());
        return new Result<>(HttpStatus.FORBIDDEN.value(), null, e.getMessage());
    }

    /**
     * 上传大小超限。
     */
    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    Result<?> uploadSizeLimitExceptionHandle(Exception e) {
        log.warn("上传文件大小超过限制", e);
        String msg = "上传文件失败：文件大小超过系统限制，请压缩或分批上传。如需上传更大文件，请联系系统管理员调整上传大小限制。";
        return new Result<>(HttpStatus.BAD_REQUEST.value(), null, msg);
    }
}
