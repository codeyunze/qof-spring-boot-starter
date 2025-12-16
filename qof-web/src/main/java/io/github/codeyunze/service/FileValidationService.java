package io.github.codeyunze.service;

import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口（Web适配层）
 * 只负责将Web层的MultipartFile转换为core层需要的DTO
 * 所有校验逻辑都在core层的 AbstractQofClient.upload 中统一处理
 *
 * @author 高晗
 * @since 2025/2/25
 */
public interface FileValidationService {

    /**
     * 构建文件信息DTO
     * 将Web层的MultipartFile和QofFileUploadDto转换为QofFileInfoDto
     *
     * @param file          文件
     * @param fileUploadDto 文件上传 DTO
     * @return 文件信息 DTO
     */
    QofFileInfoDto<?> buildFileInfoDto(MultipartFile file, QofFileUploadDto fileUploadDto);

    /**
     * 创建流式响应体
     *
     * @param inputStream 输入流
     * @param fileId      文件 ID
     * @param operation   操作类型（用于日志）
     * @return 流式响应体
     */
    org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody createStreamingResponseBody(
            java.io.InputStream inputStream, Long fileId, String operation);

    /**
     * 对文件名进行 URL 编码
     *
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    String encodeFileName(String fileName);
}

