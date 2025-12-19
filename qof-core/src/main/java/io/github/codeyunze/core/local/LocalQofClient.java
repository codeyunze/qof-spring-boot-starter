package io.github.codeyunze.core.local;

import io.github.codeyunze.QofConstant;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofFileOperationBase;
import io.github.codeyunze.core.StorageStationHelper;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.FileDownloadException;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return StorageStationHelper.getConfigValue(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation(),
                (v) -> fileProperties.getFilepath(),
                LocalQofConfig::getFilepath,
                "filepath"
        );
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto<?> info) {
        // 确保上传目录存在，使用Path.normalize()规范化路径
        Path basePath = Paths.get(getFilePath(info)).toAbsolutePath().normalize();
        
        // 清理directoryAddress，移除前导的/或\，确保它是相对路径
        String directoryAddress = info.getDirectoryAddress();
        if (directoryAddress != null) {
            // 移除前导的/或\，以及Windows路径分隔符
            directoryAddress = directoryAddress.replaceFirst("^[/\\\\]+", "");
        }
        
        Path uploadPath = basePath.resolve(directoryAddress).normalize();
        
        // 验证路径安全性，防止路径遍历攻击（使用绝对路径进行比较，确保跨平台兼容）
        if (!uploadPath.startsWith(basePath)) {
            log.error("路径遍历攻击检测，基础路径: {}, 目标路径: {}", basePath, uploadPath);
            throw new FileUploadException("文件上传失败，请稍后重试", new SecurityException("非法路径"));
        }
        
        if (!Files.exists(uploadPath)) {
            try {
                // 创建目录
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("创建上传目录失败: {}", uploadPath, e);
                throw new FileUploadException("文件上传失败，请稍后重试", e);
            }
        }

        // 从filePath中提取扩展名（filePath已经在AbstractQofClient中设置，格式为：directoryAddress/fileId.suffix）
        String suffix = "";
        String filePathStr = info.getFilePath();
        if (filePathStr != null && filePathStr.contains(".")) {
            int lastDotIndex = filePathStr.lastIndexOf(".");
            if (lastDotIndex >= 0 && lastDotIndex < filePathStr.length() - 1) {
                suffix = filePathStr.substring(lastDotIndex);
            }
        }
        
        // 如果filePath中没有扩展名，尝试从fileName中提取
        if (suffix.isEmpty() && info.getFileName() != null && info.getFileName().contains(".")) {
            int lastDotIndex = info.getFileName().lastIndexOf(".");
            if (lastDotIndex >= 0 && lastDotIndex < info.getFileName().length() - 1) {
                suffix = info.getFileName().substring(lastDotIndex);
            }
        }
        
        String fileName = info.getFileId() + suffix;

        // 定义目标文件路径，使用normalize()规范化
        Path filePath = uploadPath.resolve(fileName).normalize();
        
        // 再次验证路径安全性（使用绝对路径进行比较）
        if (!filePath.startsWith(basePath)) {
            log.error("路径遍历攻击检测，基础路径: {}, 目标路径: {}", basePath, filePath);
            throw new FileUploadException("文件上传失败，请稍后重试", new SecurityException("非法路径"));
        }
        
        try (InputStream inputStream = fis) {
            // 使用NIO将输入流复制到目标文件，如果文件已经存在，则覆盖
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件上传失败，文件路径: {}", filePath, e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo) {
        // 使用Path.normalize()规范化路径，转换为绝对路径以确保跨平台兼容
        Path basePath = Paths.get(getFilePath(fileBo)).toAbsolutePath().normalize();
        
        // 清理filePath，移除前导的/或\，确保它是相对路径
        String filePathStr = fileBo.getFilePath();
        if (filePathStr != null) {
            filePathStr = filePathStr.replaceFirst("^[/\\\\]+", "");
        }
        
        Path filePath = basePath.resolve(filePathStr).normalize();
        
        // 验证路径安全性（使用绝对路径进行比较）
        if (!filePath.startsWith(basePath)) {
            log.error("路径遍历攻击检测，基础路径: {}, 目标路径: {}", basePath, filePath);
            throw new FileDownloadException("文件下载失败，请稍后重试", new SecurityException("非法路径"));
        }
        
        if (!Files.exists(filePath)) {
            log.warn("文件不存在，文件路径: {}", filePath);
            throw new DataNotExistException("文件不存在");
        }

        QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
        BeanUtils.copyProperties(fileBo, fileDownloadBo);
        try {
            fileDownloadBo.setInputStream(Files.newInputStream(filePath));
        } catch (IOException e) {
            log.error("下载文件时发生错误，文件路径: {}", filePath, e);
            throw new FileDownloadException("文件下载失败，请稍后重试", e);
        }

        return fileDownloadBo;
    }

    @Override
    protected boolean doDelete(QofFileInfoBo<?> fileBo) {
        // 使用Path.normalize()规范化路径，转换为绝对路径以确保跨平台兼容
        Path basePath = Paths.get(getFilePath(fileBo)).toAbsolutePath().normalize();
        
        // 清理filePath，移除前导的/或\，确保它是相对路径
        String filePathStr = fileBo.getFilePath();
        if (filePathStr != null) {
            filePathStr = filePathStr.replaceFirst("^[/\\\\]+", "");
        }
        
        Path filePath = basePath.resolve(filePathStr).normalize();
        
        // 验证路径安全性（使用绝对路径进行比较）
        if (!filePath.startsWith(basePath)) {
            log.error("路径遍历攻击检测，基础路径: {}, 目标路径: {}", basePath, filePath);
            return false;
        }
        
        if (!Files.exists(filePath)) {
            return true;
        }
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败，文件路径: {}", filePath, e);
            return false;
        }
    }
}
