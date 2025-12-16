package io.github.codeyunze.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.service.FileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 文件服务实现（Web适配层）
 * 只负责将Web层的MultipartFile转换为core层需要的DTO
 * 所有校验逻辑都在core层的 AbstractQofClient.upload 中统一处理
 *
 * @author 高晗
 * @since 2025/2/25
 */
@Service
public class FileValidationServiceImpl implements FileValidationService {

    private static final Logger log = LoggerFactory.getLogger(FileValidationServiceImpl.class);

    @Resource
    private QofProperties qofProperties;

    @Override
    public QofFileInfoDto<?> buildFileInfoDto(MultipartFile file, QofFileUploadDto fileUploadDto) {
        QofFileInfoDto<?> fileInfoDto = new QofFileInfoDto<>();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        // 获取文件名
        String fileName;
        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            fileName = file.getOriginalFilename();
        } else {
            fileName = fileUploadDto.getFileName();
        }

        fileInfoDto.setFileName(fileName);
        fileInfoDto.setFileType(file.getContentType());
        fileInfoDto.setFileSize(file.getSize());

        // 构建安全的目录地址（由系统自动生成，防止路径遍历攻击）
        String directoryAddress = "/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN);
        fileInfoDto.setDirectoryAddress(directoryAddress);

        return fileInfoDto;
    }


    @Override
    public org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody createStreamingResponseBody(
            InputStream inputStream, Long fileId, String operation) {
        return outputStream -> {
            try (InputStream is = inputStream) {
                int bufferSize = qofProperties != null && qofProperties.getBufferSize() > 0
                        ? qofProperties.getBufferSize()
                        : 8192; // 默认8KB
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                log.error("文件{}流处理异常，文件Id: {}", operation, fileId, e);
                throw new RuntimeException("文件" + operation + "流处理异常", e);
            }
        };
    }

    @Override
    public String encodeFileName(String fileName) {
        try {
            return java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            log.warn("文件名编码失败: {}", fileName, e);
            return fileName; // 编码失败时返回原文件名
        }
    }
}

