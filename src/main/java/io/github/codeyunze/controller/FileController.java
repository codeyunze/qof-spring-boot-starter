package io.github.codeyunze.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileDeleteDto;
import io.github.codeyunze.dto.QofFileDownloadDto;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * @author yunze
 * @since 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private final QofClientFactory qofClientFactory;

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
    @PutMapping("upload")
    public String upload(@RequestParam("uploadfile") MultipartFile file
            , @Valid QofFileUploadDto fileUploadDto) {
        QofFileInfoDto fileInfoDto = new QofFileInfoDto();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            // 得到文件名
            String fileName = file.getOriginalFilename();
            fileInfoDto.setFileName(fileName);
        }
        fileInfoDto.setFileType(file.getContentType());
        fileInfoDto.setDirectoryAddress("/default/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN));
        fileInfoDto.setFileSize(file.getSize());

        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            return String.valueOf(client.upload(file.getInputStream(), fileInfoDto));
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败，异常信息", e);
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileDownloadDto 下载文件信息
     * @return 文件流信息
     */
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(@RequestBody @Valid QofFileDownloadDto fileDownloadDto) {
        QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileDownloadDto.getFileStorageMode()).download(fileDownloadDto.getFileId());

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileDownloadBo.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                // 告诉浏览器文件的大小，以显示文件的下载进度
                .contentLength(fileDownloadBo.getFileSize())
                .body(streamingResponseBody);
    }


    /**
     * 删除文件
     *
     * @param fileDeleteDto 删除信息
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestBody @Valid QofFileDeleteDto fileDeleteDto) {
        boolean deleted = qofClientFactory.buildClient(fileDeleteDto.getFileStorageMode()).delete(fileDeleteDto.getFileId());
        return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
    }
}
