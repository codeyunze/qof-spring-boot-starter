package io.github.codeyunze.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
     * 文件名安全校验正则：只允许字母、数字、下划线、中划线、点和空格
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\s.]+$");
    
    /**
     * 路径遍历攻击检测：检测是否包含 .. 或 /
     */
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile("(\\.\\.|[/\\\\])");

    private final QofClientFactory qofClientFactory;

    public FileController(QofClientFactory qofClientFactory) {
        this.qofClientFactory = qofClientFactory;
    }
    
    /**
     * 验证文件名安全性
     *
     * @param fileName 文件名
     * @return true表示安全，false表示不安全
     */
    private boolean isSafeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        // 检查是否包含路径遍历字符
        if (PATH_TRAVERSAL_PATTERN.matcher(fileName).find()) {
            return false;
        }
        // 检查文件名格式
        return SAFE_FILENAME_PATTERN.matcher(fileName).matches();
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
            return new Result<>(HttpStatus.BAD_REQUEST.value(), null, "文件名包含非法字符，请使用字母、数字、下划线、中划线和点");
        }
        
        fileInfoDto.setFileName(fileName);
        fileInfoDto.setFileType(file.getContentType());
        
        // 构建安全的目录地址（由系统自动生成，防止路径遍历攻击）
        String directoryAddress = "/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN);
        fileInfoDto.setDirectoryAddress(directoryAddress);
        fileInfoDto.setFileSize(file.getSize());

        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            Long fileId = client.upload(file.getInputStream(), fileInfoDto);
            return new Result<>(HttpStatus.OK.value(), fileId, "文件上传成功");
        } catch (IOException e) {
            log.error("文件上传失败，文件名: {}", fileName, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败，文件名: {}", fileName, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败: " + e.getMessage());
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
                    byte[] buffer = new byte[8192]; // 增大缓冲区提高性能
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
                    byte[] buffer = new byte[8192]; // 增大缓冲区提高性能
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
