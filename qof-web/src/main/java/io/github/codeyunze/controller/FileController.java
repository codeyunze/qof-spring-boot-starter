package io.github.codeyunze.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.utils.FileTypeDetector;
import io.github.codeyunze.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * @author 高晗
 * @since 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    
    /**
     * 路径遍历攻击检测：检测是否包含 .. 或 /
     */
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile("(\\.\\.|[/\\\\])");
    
    /**
     * 危险字符检测：检测控制字符、不可见字符等
     */
    private static final Pattern DANGEROUS_CHAR_PATTERN = Pattern.compile("[\\x00-\\x1F\\x7F]");

    private final QofClientFactory qofClientFactory;

    @Resource
    private QofProperties qofProperties;

    public FileController(QofClientFactory qofClientFactory) {
        this.qofClientFactory = qofClientFactory;
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
        if (fileName.length() > 255) {
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
    

    /**
     * 文件上传接口
     *
     * @param file          文件
     * @param fileUploadDto 文件信息
     * @return 文件Id
     */
    @PostMapping("upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file
            , @Valid QofFileUploadDto fileUploadDto) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            log.warn("上传文件为空");
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, "上传文件不能为空");
        }

        QofFileInfoDto<?> fileInfoDto = new QofFileInfoDto<>();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        // 获取并验证文件名
        String fileName;
        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            fileName = file.getOriginalFilename();
        } else {
            fileName = fileUploadDto.getFileName();
        }
        
        // 验证文件名安全性
        if (!isSafeFileName(fileName)) {
            log.warn("文件名不安全: {}", fileName);
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, "文件名包含非法字符或格式不正确，请确保文件名不包含路径分隔符、控制字符等危险字符");
        }
        
        // 验证文件大小
        long fileSize = file.getSize();
        if (qofProperties.getMaxFileSize() > 0 && fileSize > qofProperties.getMaxFileSize()) {
            log.warn("文件大小超过限制: {} bytes, 最大允许: {} bytes", fileSize, qofProperties.getMaxFileSize());
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, 
                    String.format("文件大小超过限制，最大允许: %.2f MB", qofProperties.getMaxFileSize() / 1024.0 / 1024.0));
        }
        
        fileInfoDto.setFileName(fileName);
        String contentType = file.getContentType();
        fileInfoDto.setFileType(contentType);
        
        // Magic Number检测：验证文件真实类型
        if (qofProperties.isEnableMagicNumberDetection()) {
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
        }
        
        // 构建安全的目录地址（由系统自动生成，防止路径遍历攻击）
        String directoryAddress = "/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN);
        fileInfoDto.setDirectoryAddress(directoryAddress);
        fileInfoDto.setFileSize(fileSize);

        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            Long fileId = client.upload(file.getInputStream(), fileInfoDto);
            return new Result<>(HttpStatus.OK.value(), fileId, "文件上传成功");
        } catch (IOException e) {
            log.error("文件上传失败，文件名: {}", fileName, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败，请稍后重试");
        } catch (Exception e) {
            log.error("文件上传失败，文件名: {}", fileName, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败，请稍后重试");
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileId          文件唯一Id
     * @param fileStorageMode 文件存储模式
     * @return 文件流信息
     */
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode) {
        try {
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).download(fileId);

            StreamingResponseBody streamingResponseBody = outputStream -> {
                try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                    int bufferSize = qofProperties != null && qofProperties.getBufferSize() > 0 
                            ? qofProperties.getBufferSize() 
                            : 8192; // 默认8KB
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    log.error("文件下载流处理异常，文件Id: {}", fileId, e);
                    throw new RuntimeException("文件下载流处理异常", e);
                }
            };

            // 对文件名进行URL编码，防止特殊字符问题
            String encodedFileName = java.net.URLEncoder.encode(
                    fileDownloadBo.getFileName(), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    // 告诉浏览器文件的大小，以显示文件的下载进度
                    .contentLength(fileDownloadBo.getFileSize())
                    .body(streamingResponseBody);
        } catch (Exception e) {
            log.error("文件下载失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 预览文件
     *
     * @param fileId          文件唯一Id
     * @param fileStorageMode 文件存储的策略 {@link SysFiles#getFileStorageMode()}
     * @return 文件流信息
     */
    @GetMapping("preview")
    public ResponseEntity<StreamingResponseBody> preview(@RequestParam("fileId") Long fileId, @RequestParam("fileStorageMode") String fileStorageMode) {
        try {
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).preview(fileId);

            StreamingResponseBody streamingResponseBody = outputStream -> {
                try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                    int bufferSize = qofProperties != null && qofProperties.getBufferSize() > 0 
                            ? qofProperties.getBufferSize() 
                            : 8192; // 默认8KB
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    log.error("文件预览流处理异常，文件Id: {}", fileId, e);
                    throw new RuntimeException("文件预览流处理异常", e);
                }
            };

            // 对文件名进行URL编码
            String encodedFileName = java.net.URLEncoder.encode(
                    fileDownloadBo.getFileName(), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                    .filename(encodedFileName, StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .contentType(MediaType.parseMediaType(fileDownloadBo.getFileType()))
                    .body(streamingResponseBody);
        } catch (Exception e) {
            log.error("文件预览失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     *
     * @param fileId          文件唯一Id
     * @param fileStorageMode 文件存储模式
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestParam("fileId") Long fileId, @RequestParam("fileStorageMode") String fileStorageMode) {
        try {
            boolean deleted = qofClientFactory.buildClient(fileStorageMode).delete(fileId);
            return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
        } catch (Exception e) {
            log.error("文件删除失败，文件Id: {}", fileId, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败: " + e.getMessage());
        }
    }

}
