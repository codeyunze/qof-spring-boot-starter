package io.github.codeyunze.service;

import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件校验服务接口
 *
 * @author 高晗
 * @since 2025/2/25
 */
public interface FileValidationService {

    /**
     * 验证上传文件
     *
     * @param file 文件
     * @return 验证结果，如果验证失败返回错误结果，否则返回null
     */
    Result<Long> validateUploadFile(MultipartFile file);

    /**
     * 获取并验证文件名
     *
     * @param file          文件
     * @param fileUploadDto 文件上传DTO
     * @return 验证结果，如果验证失败返回错误结果，否则返回文件名
     */
    Object validateFileName(MultipartFile file, QofFileUploadDto fileUploadDto);

    /**
     * 验证文件大小
     *
     * @param fileSize 文件大小
     * @return 验证结果，如果验证失败返回错误结果，否则返回null
     */
    Result<Long> validateFileSize(long fileSize);

    /**
     * 验证文件类型（Magic Number检测）
     *
     * @param file        文件
     * @param contentType 内容类型
     * @param fileName    文件名
     * @return 验证结果，如果验证失败返回错误结果，否则返回null
     */
    Result<Long> validateFileType(MultipartFile file, String contentType, String fileName);

    /**
     * 构建文件信息DTO
     *
     * @param fileUploadDto 文件上传DTO
     * @param fileName      文件名
     * @param contentType   内容类型
     * @param fileSize      文件大小
     * @return 文件信息DTO
     */
    QofFileInfoDto<?> buildFileInfoDto(QofFileUploadDto fileUploadDto, String fileName,
                                       String contentType, long fileSize);

    /**
     * 创建流式响应体
     *
     * @param inputStream 输入流
     * @param fileId      文件ID
     * @param operation   操作类型（用于日志）
     * @return 流式响应体
     */
    org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody createStreamingResponseBody(
            java.io.InputStream inputStream, Long fileId, String operation);

    /**
     * 对文件名进行URL编码
     *
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    String encodeFileName(String fileName);
}

