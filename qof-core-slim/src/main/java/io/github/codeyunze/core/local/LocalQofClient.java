package io.github.codeyunze.core.local;

import io.github.codeyunze.QofConstant;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofFileOperationBase;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件操作接口实现
 *
 * @author 高晗
 * @since 2025/2/17 16:53
 */
@Service
@ConditionalOnProperty(prefix = "qof.local", name = QofConstant.ENABLE, havingValue = QofConstant.ENABLE_VALUE)
public class LocalQofClient extends AbstractQofClient {

    private static final Logger log = LoggerFactory.getLogger(LocalQofClient.class);

    @Resource
    private LocalQofProperties fileProperties;

    public LocalQofClient(QofExtService qofExtService) {
        super(qofExtService);
    }

    private String getFilePath(QofFileOperationBase fileOperationBase) {
        String fileStorageStation;
        if (fileProperties.getMultiple().containsKey(fileOperationBase.getFileStorageStation())) {
            fileStorageStation = fileOperationBase.getFileStorageStation();
        } else {
            fileStorageStation = fileProperties.getDefaultStorageStation();
        }
        return fileProperties.getMultiple().get(fileStorageStation).getFilepath();
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto<?> info) {
        // 确保上传目录存在
        Path uploadPath = Paths.get(getFilePath(info) + info.getDirectoryAddress());
        if (!Files.exists(uploadPath)) {
            try {
                // 创建目录
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("创建上传目录失败: {}", uploadPath, e);
                throw new RuntimeException("创建上传目录失败: " + uploadPath, e);
            }
        }

        String fileName = info.getFileId() + info.getFilePath().substring(info.getFilePath().lastIndexOf("."));

        // 定义目标文件路径
        Path filePath = uploadPath.resolve(fileName);
        try (InputStream inputStream = fis) {
            // 使用NIO将输入流复制到目标文件，如果文件已经存在，则覆盖
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件上传失败，文件路径: {}", filePath, e);
            throw new RuntimeException("文件上传失败: " + filePath, e);
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo) {
        // 确保文件路径正确构建
        String filePath = getFilePath(fileBo) + fileBo.getFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            log.warn("文件不存在，文件路径: {}", filePath);
            throw new DataNotExistException("文件不存在");
        }

        QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
        BeanUtils.copyProperties(fileBo, fileDownloadBo);
        try {
            fileDownloadBo.setInputStream(Files.newInputStream(Paths.get(file.getPath())));
        } catch (IOException e) {
            log.error("下载文件时发生错误，文件路径: {}", filePath, e);
            throw new RuntimeException("下载文件时发生错误: " + filePath, e);
        }

        return fileDownloadBo;
    }

    @Override
    protected boolean doDelete(QofFileInfoBo<?> fileBo) {
        // 确保文件路径正确构建
        String filePath = getFilePath(fileBo) + fileBo.getFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }
}
