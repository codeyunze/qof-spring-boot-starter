package io.github.codeyunze.controller;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.exception.FileAccessDeniedException;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.spi.FileMetadataQuery;
import io.github.codeyunze.spi.FileMetadataRepository;
import io.github.codeyunze.spi.metadata.FileMetadata;
import io.github.codeyunze.spi.metadata.FileMetadataQueryCriteria;
import io.github.codeyunze.spi.metadata.PageResult;
import io.github.codeyunze.utils.Result;
import io.github.codeyunze.utils.ResultTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;

/**
 * 内置文件 HTTP API。
 * <p>
 * 默认关闭，需配置 {@code qof.web.enabled=true}；路径前缀可通过 {@code qof.web.base-path} 调整。
 */
@RestController
@RequestMapping("${qof.web.base-path:/file}")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final QofClientFactory qofClientFactory;
    private final FileMetadataRepository metadataRepository;
    private final ObjectProvider<FileMetadataQuery> metadataQueryProvider;

    @Resource
    private FileValidationService fileValidationService;

    public FileController(QofClientFactory qofClientFactory,
                          FileMetadataRepository metadataRepository,
                          ObjectProvider<FileMetadataQuery> metadataQueryProvider) {
        this.qofClientFactory = qofClientFactory;
        this.metadataRepository = metadataRepository;
        this.metadataQueryProvider = metadataQueryProvider;
    }

    @PostMapping("upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file,
                               @Valid QofFileUploadDto fileUploadDto) {
        QofFileInfoDto<?> fileInfoDto = fileValidationService.buildFileInfoDto(file, fileUploadDto);
        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            Long fileId = client.upload(file.getInputStream(), fileInfoDto);
            return new Result<>(HttpStatus.OK.value(), fileId, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败，文件名: {}", fileInfoDto.getFileName(), e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败，请稍后重试");
        }
    }

    @GetMapping("page")
    public Result<ResultTable<FileMetadata>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "fileStorageMode", required = false) String fileStorageMode,
            @RequestParam(value = "fileStorageStation", required = false) String fileStorageStation
    ) {
        FileMetadataQuery query = metadataQueryProvider.getIfAvailable();
        if (query == null) {
            return new Result<>(HttpStatus.NOT_IMPLEMENTED.value(), null,
                    "未提供 FileMetadataQuery，列表能力不可用。请引入 qof-spring-boot-starter-persistence-mysql 或自行实现 FileMetadataQuery");
        }
        FileMetadataQueryCriteria criteria = new FileMetadataQueryCriteria();
        criteria.setPageNum(pageNum);
        criteria.setPageSize(pageSize);
        criteria.setFileName(fileName);
        criteria.setFileStorageMode(fileStorageMode);
        criteria.setFileStorageStation(fileStorageStation);
        PageResult<FileMetadata> page = query.page(criteria);
        return new Result<>(HttpStatus.OK.value(), new ResultTable<>(page.getRecords(), page.getTotal()), "查询成功");
    }

    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            checkFileAccessPermission(fileId, createId);
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).download(fileId);
            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "下载");
            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileDownloadBo.getFileSize())
                    .body(streamingResponseBody);
        } catch (FileAccessDeniedException e) {
            log.warn("文件下载权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("文件下载失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("preview")
    public ResponseEntity<StreamingResponseBody> preview(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            checkFileAccessPermission(fileId, createId);
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
        } catch (FileAccessDeniedException e) {
            log.warn("文件预览权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("文件预览失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("delete")
    public Result<Boolean> delete(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            checkFileAccessPermission(fileId, createId);
            boolean deleted = qofClientFactory.buildClient(fileStorageMode).delete(fileId);
            return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
        } catch (Exception e) {
            log.error("文件删除失败，文件Id: {}", fileId, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败: " + e.getMessage());
        }
    }

    private void checkFileAccessPermission(Long fileId, Long createId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileAccessDeniedException("文件访问被拒绝：文件不存在"));
        if (metadata.getPublicAccess() != null && metadata.getPublicAccess() == 1) {
            return;
        }
        if (createId == null) {
            throw new FileAccessDeniedException("文件访问被拒绝：该文件为私有文件，需要提供创建者ID");
        }
        if (metadata.getCreateId() == null || !metadata.getCreateId().equals(createId)) {
            throw new FileAccessDeniedException("文件访问被拒绝：创建者ID不匹配");
        }
    }
}
