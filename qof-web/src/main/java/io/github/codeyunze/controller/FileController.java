package io.github.codeyunze.controller;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 高晗
 * @since 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final QofClientFactory qofClientFactory;

    @Resource
    private FileValidationService fileValidationService;

    public FileController(QofClientFactory qofClientFactory) {
        this.qofClientFactory = qofClientFactory;
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
        Result<Long> validationResult = fileValidationService.validateUploadFile(file);
        if (validationResult != null) {
            return validationResult;
        }

        // 获取并验证文件名
        Object fileNameResult = fileValidationService.validateFileName(file, fileUploadDto);
        if (fileNameResult instanceof Result) {
            @SuppressWarnings("unchecked")
            Result<Long> errorResult = (Result<Long>) fileNameResult;
            return errorResult;
        }
        String fileName = (String) fileNameResult;

        // 验证文件大小
        long fileSize = file.getSize();
        validationResult = fileValidationService.validateFileSize(fileSize);
        if (validationResult != null) {
            return validationResult;
        }

        // 验证文件类型
        String contentType = file.getContentType();
        validationResult = fileValidationService.validateFileType(file, contentType, fileName);
        if (validationResult != null) {
            return validationResult;
        }

        // 构建文件信息DTO
        QofFileInfoDto<?> fileInfoDto = fileValidationService.buildFileInfoDto(
                fileUploadDto, fileName, contentType, fileSize);

        // 执行文件上传
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

            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "下载");

            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
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

            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "预览");

            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());

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
