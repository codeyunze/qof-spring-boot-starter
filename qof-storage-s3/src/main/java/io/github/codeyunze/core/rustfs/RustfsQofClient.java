package io.github.codeyunze.core.rustfs;

import io.github.codeyunze.QofConstant;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofFileOperationBase;
import io.github.codeyunze.core.StorageStationHelper;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.FileDownloadException;
import io.github.codeyunze.exception.FileDeleteException;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.spi.ObjectStorageProvider;
import io.github.codeyunze.utils.StrUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * RustFS文件操作接口实现
 *
 * @author 高晗
 * @since 2025/1/12
 */
@ConditionalOnProperty(prefix = "qof.rustfs", name = QofConstant.ENABLE, havingValue = QofConstant.ENABLE_VALUE)
public class RustfsQofClient extends AbstractQofClient implements ObjectStorageProvider {

    @Override
    public String mode() {
        return QofConstant.StorageMode.RUSTFS;
    }

    @Override
    public QofClient getClient() {
        return this;
    }

    private static final Logger log = LoggerFactory.getLogger(RustfsQofClient.class);

    @Resource
    private RustfsQofProperties fileProperties;

    @Resource
    private Map<String, S3Client> s3ClientMap;

    public RustfsQofClient(QofExtService qofExtService) {
        super(qofExtService);
    }

    private S3Client getClient(QofFileOperationBase fileOperationBase) {
        String fileStorageStation = StorageStationHelper.getStorageStation(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation()
        );
        String clientKey = fileStorageStation + StrUtils.toUpperCase(QofConstant.StorageMode.RUSTFS);
        S3Client client = s3ClientMap.get(clientKey);
        if (client == null) {
            throw new IllegalStateException("未找到RustFS S3客户端，存储站: " + fileStorageStation);
        }
        return client;
    }

    private String getBucketName(QofFileOperationBase fileOperationBase) {
        return StorageStationHelper.getConfigValue(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation(),
                (v) -> fileProperties.getBucketName(),
                RustfsQofConfig::getBucketName,
                "bucket-name"
        );
    }

    private String getFilePath(QofFileOperationBase fileOperationBase) {
        String filepath = StorageStationHelper.getConfigValue(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation(),
                (v) -> fileProperties.getFilepath(),
                RustfsQofConfig::getFilepath,
                "filepath"
        );
        
        // 构建完整路径
        String fullPath = filepath + fileOperationBase.getFilePath();
        
        // S3对象名称不能以/开头，需要去掉开头的斜杠
        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }
        
        return fullPath;
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto<?> info) {
        // 获取实际使用的存储站（如果用户传入的存储站在配置中不存在，会使用默认存储站）
        // 更新到info中，确保保存到数据库时使用的是实际使用的存储站
        String actualStorageStation = StorageStationHelper.getStorageStation(
                info,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation()
        );
        info.setFileStorageStation(actualStorageStation);
        
        try (InputStream inputStream = fis) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(getBucketName(info))
                    .key(getFilePath(info))
                    .contentLength(info.getFileSize())
                    .contentType(info.getFileType())
                    .build();
            
            S3Client client = getClient(info);
            client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, info.getFileSize()));
        } catch (S3Exception e) {
            log.error("RustFS S3服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(info), e.awsErrorDetails().errorCode(), e.getMessage(), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        } catch (IOException e) {
            log.error("文件流处理异常，文件路径: {}", getFilePath(info), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        } catch (Exception e) {
            log.error("文件上传异常，文件路径: {}", getFilePath(info), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(getBucketName(fileBo))
                    .key(getFilePath(fileBo))
                    .build();
            
            S3Client client = getClient(fileBo);
            InputStream inputStream = client.getObjectAsBytes(getObjectRequest).asInputStream();

            QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
            BeanUtils.copyProperties(fileBo, fileDownloadBo);
            fileDownloadBo.setInputStream(inputStream);
            return fileDownloadBo;
        } catch (NoSuchKeyException e) {
            log.error("RustFS S3文件不存在，文件路径: {}", getFilePath(fileBo), e);
            throw new FileDownloadException("文件不存在", e);
        } catch (S3Exception e) {
            log.error("RustFS S3服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.awsErrorDetails().errorCode(), e.getMessage(), e);
            throw new FileDownloadException("文件下载失败，请稍后重试", e);
        } catch (Exception e) {
            log.error("文件下载异常，文件路径: {}", getFilePath(fileBo), e);
            throw new FileDownloadException("文件下载失败，请稍后重试", e);
        }
    }

    @Override
    protected boolean doDelete(QofFileInfoBo<?> fileBo) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(getBucketName(fileBo))
                    .key(getFilePath(fileBo))
                    .build();
            
            S3Client client = getClient(fileBo);
            client.deleteObject(deleteObjectRequest);
            return true;
        } catch (S3Exception e) {
            log.error("RustFS S3服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.awsErrorDetails().errorCode(), e.getMessage(), e);
            throw new FileDeleteException("文件删除失败，请稍后重试", e);
        } catch (Exception e) {
            log.error("文件删除异常，文件路径: {}", getFilePath(fileBo), e);
            throw new FileDeleteException("文件删除失败，请稍后重试", e);
        }
    }
}
