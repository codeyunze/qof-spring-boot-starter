package io.github.codeyunze.controller;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.SysFilesMetaBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.exception.FileAccessDeniedException;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.service.SysFilesService;
import io.github.codeyunze.utils.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.codeyunze.utils.ResultTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    @Resource
    private SysFilesService sysFilesService;

    public FileController(QofClientFactory qofClientFactory) {
        this.qofClientFactory = qofClientFactory;
    }

    /**
     * 文件上传接口
     *
     * @param file          文件
     * @param fileUploadDto 文件信息
     * @return 文件 Id
     */
    @PostMapping("upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file
            , @Valid QofFileUploadDto fileUploadDto) {
        // 构建文件信息DTO（适配层，只做数据转换，不做校验）
        QofFileInfoDto<?> fileInfoDto = fileValidationService.buildFileInfoDto(file, fileUploadDto);

        // 执行文件上传（所有校验都在 AbstractQofClient.upload 中统一处理）
        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            Long fileId = client.upload(file.getInputStream(), fileInfoDto);
            return new Result<>(HttpStatus.OK.value(), fileId, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败，文件名: {}", fileInfoDto.getFileName(), e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败，请稍后重试");
        }
    }

    /**
     * 文件分页列表（仅返回文件元数据信息，不包含文件流）
     *
     * @param pageNum          页码（从1开始）
     * @param pageSize         每页条数
     * @param fileName         文件名（模糊匹配，可选）
     * @param fileStorageMode  存储模式（local/cos/oss，可选）
     * @param fileStorageStation 存储站（可选）
     * @return 分页结果
     */
    @GetMapping("page")
    public Result<ResultTable<SysFilesMetaBo>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "fileStorageMode", required = false) String fileStorageMode,
            @RequestParam(value = "fileStorageStation", required = false) String fileStorageStation
    ) {
        IPage<SysFilesMetaBo> page = sysFilesService.pageFiles(
                new Page<>(pageNum, pageSize),
                fileName,
                fileStorageMode,
                fileStorageStation
        );
        return new Result<>(HttpStatus.OK.value(), new ResultTable<>(page.getRecords(), page.getTotal()), "查询成功");
    }

    /**
     * 文件下载接口
     *
     * @param fileId          文件唯一 Id
     * @param fileStorageMode 文件存储模式
     * @param createId        创建者ID（可选，当文件不公开时必须提供）
     * @return 文件流信息
     */
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            // 校验文件访问权限
            sysFilesService.checkFileAccessPermission(fileId, createId);
            
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).download(fileId);

            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "下载");

            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName)
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

    /**
     * 预览文件
     *
     * @param fileId          文件唯一 Id
     * @param fileStorageMode 文件存储的策略 {@link SysFiles#getFileStorageMode()}
     * @param createId        创建者ID（可选，当文件不公开时必须提供）
     * @return 文件流信息
     */
    @GetMapping("preview")
    public ResponseEntity<StreamingResponseBody> preview(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            // 校验文件访问权限
            sysFilesService.checkFileAccessPermission(fileId, createId);
            
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

    /**
     * 删除文件
     *
     * @param fileId          文件唯一 Id
     * @param fileStorageMode 文件存储模式
     * @param createId        创建者ID（可选，当文件不公开时必须提供）
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @DeleteMapping("delete")
    public Result<Boolean> delete(
            @RequestParam("fileId") Long fileId,
            @RequestParam("fileStorageMode") String fileStorageMode,
            @RequestParam(value = "createId", required = false) Long createId) {
        try {
            // 校验文件访问权限
            sysFilesService.checkFileAccessPermission(fileId, createId);
            
            boolean deleted = qofClientFactory.buildClient(fileStorageMode).delete(fileId);
            return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
        } catch (Exception e) {
            log.error("文件删除失败，文件Id: {}", fileId, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败: " + e.getMessage());
        }
    }

}
