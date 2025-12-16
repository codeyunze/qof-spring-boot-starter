package io.github.codeyunze.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.utils.FileTypeDetector;
import io.github.codeyunze.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 文件校验服务实现
 *
 * @author 高晗
 * @since 2025/2/25
 */
@Service
public class FileValidationServiceImpl implements FileValidationService {

    private static final Logger log = LoggerFactory.getLogger(FileValidationServiceImpl.class);

    /**
     * 文件名最大长度限制
     */
    private static final int MAX_FILE_NAME_LENGTH = 255;

    /**
     * 路径遍历攻击检测：检测是否包含 .. 或 /
     */
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile("(\\.\\.|[/\\\\])");

    /**
     * 危险字符检测：检测控制字符、不可见字符等
     */
    private static final Pattern DANGEROUS_CHAR_PATTERN = Pattern.compile("[\\x00-\\x1F\\x7F]");

    @Resource
    private QofProperties qofProperties;

    @Override
    public Result<Long> validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("上传文件为空");
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, "上传文件不能为空");
        }
        return null;
    }

    @Override
    public Object validateFileName(MultipartFile file, QofFileUploadDto fileUploadDto) {
        String fileName;
        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            fileName = file.getOriginalFilename();
        } else {
            fileName = fileUploadDto.getFileName();
        }

        if (!isSafeFileName(fileName)) {
            log.warn("文件名不安全: {}", fileName);
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null,
                    "文件名包含非法字符或格式不正确，请确保文件名不包含路径分隔符、控制字符等危险字符");
        }

        return fileName;
    }

    @Override
    public Result<Long> validateFileSize(long fileSize) {
        if (qofProperties.getMaxFileSize() > 0 && fileSize > qofProperties.getMaxFileSize()) {
            log.warn("文件大小超过限制: {} bytes, 最大允许: {} bytes", fileSize, qofProperties.getMaxFileSize());
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null,
                    String.format("文件大小超过限制，最大允许: %.2f MB", qofProperties.getMaxFileSize() / 1024.0 / 1024.0));
        }
        return null;
    }

    @Override
    public Result<Long> validateFileType(MultipartFile file, String contentType, String fileName) {
        if (!qofProperties.isEnableMagicNumberDetection()) {
            return null;
        }

        try (InputStream inputStream = file.getInputStream()) {
            if (!FileTypeDetector.validateFileType(inputStream, contentType)) {
                log.warn("文件类型验证失败，声明类型: {}, 文件名: {}", contentType, fileName);
                return new Result<>(HttpStatus.BAD_REQUEST.value(), null,
                        "文件类型验证失败，文件内容与声明类型不匹配");
            }
        } catch (IOException e) {
            log.error("文件类型检测异常，文件名: {}", fileName, e);
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, "文件类型检测失败");
        }
        return null;
    }

    @Override
    public QofFileInfoDto<?> buildFileInfoDto(QofFileUploadDto fileUploadDto, String fileName,
                                             String contentType, long fileSize) {
        QofFileInfoDto<?> fileInfoDto = new QofFileInfoDto<>();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        fileInfoDto.setFileName(fileName);
        fileInfoDto.setFileType(contentType);
        fileInfoDto.setFileSize(fileSize);

        // 构建安全的目录地址（由系统自动生成，防止路径遍历攻击）
        String directoryAddress = "/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN);
        fileInfoDto.setDirectoryAddress(directoryAddress);

        return fileInfoDto;
    }

    /**
     * 验证文件名安全性
     * <p>
     * 允许中文字符、字母、数字、下划线、中划线、点和空格
     * 禁止路径遍历字符（..、/、\）和控制字符
     *
     * @param fileName 文件名
     * @return true表示安全，false表示不安全
     */
    private boolean isSafeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }

        // 检查文件名长度（避免过长导致问题）
        if (fileName.length() > MAX_FILE_NAME_LENGTH) {
            log.warn("文件名过长: {}", fileName);
            return false;
        }

        // 检查是否包含路径遍历字符
        if (PATH_TRAVERSAL_PATTERN.matcher(fileName).find()) {
            log.warn("文件名包含路径遍历字符: {}", fileName);
            return false;
        }

        // 检查是否包含危险的控制字符
        if (DANGEROUS_CHAR_PATTERN.matcher(fileName).find()) {
            log.warn("文件名包含危险字符: {}", fileName);
            return false;
        }

        // 检查文件名是否以点开头或结尾（可能隐藏文件）
        String trimmed = fileName.trim();
        if (trimmed.startsWith(".") || trimmed.endsWith(".")) {
            log.warn("文件名以点开头或结尾: {}", fileName);
            return false;
        }

        // 检查是否包含连续的点（可能用于路径遍历）
        if (trimmed.contains("..")) {
            log.warn("文件名包含连续的点: {}", fileName);
            return false;
        }

        return true;
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

