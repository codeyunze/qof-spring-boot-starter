package io.github.codeyunze.web.advice;

import io.github.codeyunze.web.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 上传体积超限异常处理。
 * <p>
 * {@link MaxUploadSizeExceededException} 发生在进入 Controller 之前，
 * 带 {@code basePackages} 的 Advice 捕不到，会被
 * {@code DefaultHandlerExceptionResolver} 处理成无业务体响应。
 * 因此本 Advice 不做包限制，仅处理上传超限。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class QofMultipartExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(QofMultipartExceptionHandle.class);

    private static final String MSG = "上传文件失败：文件大小超过系统限制，请压缩或分批上传。";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("上传文件大小超过限制: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new Result<>(HttpStatus.PAYLOAD_TOO_LARGE.value(), null, MSG));
    }
}
