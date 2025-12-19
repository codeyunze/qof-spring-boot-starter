package io.github.codeyunze.core.validation;

import io.github.codeyunze.QofProperties;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.utils.FileTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * 核心文件校验服务
 * 无Web依赖，可在qof-core中直接使用
 * 确保无论从web还是第三方系统调用，都会执行相同的校验逻辑
 *
 * @author 高晗
 * @since 2025/2/25
 */
@Service
public class CoreFileValidationService {

    private static final Logger log = LoggerFactory.getLogger(CoreFileValidationService.class);

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

    /**
     * 校验文件上传前的所有规则
     * 包括：文件名安全性、文件大小、文件类型（Magic Number检测）
     *
     * @param inputStream 文件输入流（如果为null，则跳过Magic Number检测）
     *                    注意：流需要支持mark/reset，以便Magic Number检测后可以重置
     * @param fileInfoDto 文件信息 DTO
     * @throws FileUploadException 如果校验失败
     */
    public void validateBeforeUpload(InputStream inputStream, QofFileInfoDto<?> fileInfoDto) {
        // 1. 校验文件名安全性
        validateFileName(fileInfoDto.getFileName());

        // 2. 校验文件大小
        validateFileSize(fileInfoDto.getFileSize());

        // 3. 校验文件类型（Magic Number检测）
        // 注意：Magic Number检测会读取流，流必须支持mark/reset以便后续重置
        if (inputStream != null && fileInfoDto.getFileType() != null) {
            if (inputStream.markSupported()) {
                validateFileType(inputStream, fileInfoDto.getFileType(), fileInfoDto.getFileName());
            } else {
                log.warn("输入流不支持mark/reset，无法进行Magic Number检测，可能存在安全风险");
                // 如果流不支持mark，我们仍然抛出异常，要求调用方提供支持mark的流
                throw new FileUploadException("输入流不支持mark/reset，无法进行文件类型验证，请使用BufferedInputStream包装");
            }
        }
    }

    /**
     * 验证文件名安全性
     *
     * @param fileName 文件名
     * @throws FileUploadException 如果文件名不安全
     */
    public void validateFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new FileUploadException("文件名不能为空");
        }

        // 检查文件名长度
        if (fileName.length() > MAX_FILE_NAME_LENGTH) {
            log.warn("文件名过长: {}", fileName);
            throw new FileUploadException("文件名超过最大长度限制（255字符）");
        }

        // 检查是否包含路径遍历字符
        if (PATH_TRAVERSAL_PATTERN.matcher(fileName).find()) {
            log.warn("文件名包含路径遍历字符: {}", fileName);
            throw new FileUploadException("文件名包含非法字符，不允许包含路径分隔符（/、\\）或路径遍历字符（..）");
        }

        // 检查是否包含危险的控制字符
        if (DANGEROUS_CHAR_PATTERN.matcher(fileName).find()) {
            log.warn("文件名包含危险字符: {}", fileName);
            throw new FileUploadException("文件名包含非法字符，不允许包含控制字符");
        }

        // 检查文件名是否以点开头或结尾（可能隐藏文件）
        String trimmed = fileName.trim();
        if (trimmed.startsWith(".") || trimmed.endsWith(".")) {
            log.warn("文件名以点开头或结尾: {}", fileName);
            throw new FileUploadException("文件名不能以点开头或结尾");
        }

        // 检查是否包含连续的点（可能用于路径遍历）
        if (trimmed.contains("..")) {
            log.warn("文件名包含连续的点: {}", fileName);
            throw new FileUploadException("文件名包含非法字符，不允许包含连续的点（..）");
        }
    }

    /**
     * 验证文件大小
     *
     * @param fileSize 文件大小（字节）
     * @throws FileUploadException 如果文件大小无效或超过限制
     */
    public void validateFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            throw new FileUploadException("文件大小无效或文件为空");
        }

        if (qofProperties != null && qofProperties.getMaxFileSize() > 0 
                && fileSize > qofProperties.getMaxFileSize()) {
            log.warn("文件大小超过限制: {} bytes, 最大允许: {} bytes", fileSize, qofProperties.getMaxFileSize());
            double maxSizeMB = qofProperties.getMaxFileSize() / 1024.0 / 1024.0;
            throw new FileUploadException(String.format("文件大小超过限制，最大允许: %.2f MB", maxSizeMB));
        }
    }

    /**
     * 验证文件类型（Magic Number检测）
     *
     * @param inputStream 文件输入流
     * @param contentType 声明的文件类型
     * @param fileName    文件名（用于日志）
     * @throws FileUploadException 如果文件类型验证失败
     */
    public void validateFileType(InputStream inputStream, String contentType, String fileName) {
        if (qofProperties == null || !qofProperties.isEnableMagicNumberDetection()) {
            return; // 如果未启用Magic Number检测，跳过
        }

        if (contentType == null || contentType.trim().isEmpty()) {
            log.warn("文件类型为空，跳过Magic Number检测，文件名: {}", fileName);
            return; // 如果内容类型为空，跳过检测
        }

        try {
            if (!FileTypeDetector.validateFileType(inputStream, contentType)) {
                log.warn("文件类型验证失败，声明类型: {}, 文件名: {}", contentType, fileName);
                throw new FileUploadException("文件类型验证失败，文件内容与声明类型不匹配");
            }
        } catch (Exception e) {
            log.error("文件类型检测异常，文件名: {}", fileName, e);
            throw new FileUploadException("文件类型检测失败: " + e.getMessage());
        }
    }
}

