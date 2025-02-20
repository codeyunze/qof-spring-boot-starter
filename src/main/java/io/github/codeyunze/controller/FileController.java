package io.github.codeyunze.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofContext;
import io.github.codeyunze.dto.QofFileDeleteDto;
import io.github.codeyunze.dto.QofFileDownloadDto;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.enums.QofStorageModeEnum;
import io.github.codeyunze.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @RequestMapping("test")
    public String test() {
        return " file test!!!";
    }

    private final QofClient qofLocalClient;
    private final QofClient qofCosClient;

    @Autowired
    public FileController(@Qualifier("localQofClient") QofClient qofLocalClient
            , @Qualifier("cosQofClient") QofClient qofCosClient) {
        this.qofLocalClient = qofLocalClient;
        this.qofCosClient = qofCosClient;
    }

    /**
     * 文件上传接口
     *
     * @param file          文件
     * @param fileUploadDto 文件信息
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
            if (QofStorageModeEnum.COS.getMode().equals(fileUploadDto.getFileStorageMode())) {
                return String.valueOf(qofCosClient.upload(file.getInputStream(), fileInfoDto));
            } else {
                return String.valueOf(qofLocalClient.upload(file.getInputStream(), fileInfoDto));
            }
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败，异常信息", e);
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileDownloadDto 下载文件信息
     */
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(@RequestBody @Valid QofFileDownloadDto fileDownloadDto) {
        QofFileDownloadBo fileDownloadBo;
        if (QofStorageModeEnum.COS.getMode().equals(fileDownloadDto.getFileStorageMode())) {
            fileDownloadBo = qofCosClient.download(fileDownloadDto.getFileId());
        } else {
            fileDownloadBo = qofLocalClient.download(fileDownloadDto.getFileId());
        }

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


    @RequestMapping("preview")
    public void preview() {

    }

    /**
     * 删除文件
     *
     * @param fileDeleteDto 删除信息
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestBody @Valid QofFileDeleteDto fileDeleteDto) {
        boolean deleted;
        if (QofStorageModeEnum.COS.getMode().equals(fileDeleteDto.getFileStorageMode())) {
            deleted = qofCosClient.delete(fileDeleteDto.getFileId());
        } else {
            deleted = qofLocalClient.delete(fileDeleteDto.getFileId());
        }
        return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
    }
}
