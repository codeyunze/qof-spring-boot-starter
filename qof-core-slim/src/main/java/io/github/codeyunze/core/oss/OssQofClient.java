package io.github.codeyunze.core.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofFileOperationBase;
import io.github.codeyunze.core.StorageStationHelper;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.FileUploadException;
import io.github.codeyunze.exception.FileDownloadException;
import io.github.codeyunze.exception.FileDeleteException;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 阿里云 OSS文件操作接口实现
 *
 * @author 高晗
 * @since 2025/2/18
 */
@Service
@ConditionalOnProperty(prefix = "qof.oss", name = QofConstant.ENABLE, havingValue = QofConstant.ENABLE_VALUE)
public class OssQofClient extends AbstractQofClient {

    private static final Logger log = LoggerFactory.getLogger(OssQofClient.class);

    @Resource
    private OssQofProperties fileProperties;

    @Resource
    private Map<String, OSS> ossClientMap;

    public OssQofClient(QofExtService qofExtService) {
        super(qofExtService);
    }

    private OSS getClient(QofFileOperationBase fileOperationBase) {
        String fileStorageStation = StorageStationHelper.getStorageStation(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation()
        );
        String clientKey = fileStorageStation + StrUtils.toUpperCase(QofConstant.StorageMode.OSS);
        OSS client = ossClientMap.get(clientKey);
        if (client == null) {
            throw new IllegalStateException("未找到OSS客户端，存储站: " + fileStorageStation);
        }
        return client;
    }

    private String getBucketName(QofFileOperationBase fileOperationBase) {
        return StorageStationHelper.getConfigValue(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation(),
                (v) -> fileProperties.getBucketName(),
                OssQofConfig::getBucketName,
                "bucket-name"
        );
    }

    private String getFilePath(QofFileOperationBase fileOperationBase) {
        String filepath = StorageStationHelper.getConfigValue(
                fileOperationBase,
                fileProperties.getMultiple(),
                fileProperties.getDefaultStorageStation(),
                (v) -> fileProperties.getFilepath(),
                OssQofConfig::getFilepath,
                "filepath"
        );
        
        // 构建完整路径
        String fullPath = filepath + fileOperationBase.getFilePath();
        
        // OSS对象名称不能以/开头，需要去掉开头的斜杠
        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }
        
        return fullPath;
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto<?> info) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        objectMetadata.setContentLength(info.getFileSize());
        
        try (InputStream inputStream = fis) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    getBucketName(info),
                    getFilePath(info),
                    inputStream,
                    objectMetadata
            );
            
            OSS client = getClient(info);
            client.putObject(putObjectRequest);
        } catch (OSSException e) {
            log.error("OSS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(info), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        } catch (ClientException e) {
            log.error("OSS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(info), e.getMessage(), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        } catch (IOException e) {
            log.error("文件流处理异常，文件路径: {}", getFilePath(info), e);
            throw new FileUploadException("文件上传失败，请稍后重试", e);
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(getBucketName(fileBo), getFilePath(fileBo));
            OSSObject ossObject = getClient(fileBo).getObject(getObjectRequest);

            QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
            BeanUtils.copyProperties(fileBo, fileDownloadBo);
            fileDownloadBo.setInputStream(ossObject.getObjectContent());
            return fileDownloadBo;
        } catch (OSSException e) {
            log.error("OSS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileDownloadException("文件下载失败，请稍后重试", e);
        } catch (ClientException e) {
            log.error("OSS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(fileBo), e.getMessage(), e);
            throw new FileDownloadException("文件下载失败，请稍后重试", e);
        }
    }

    @Override
    protected boolean doDelete(QofFileInfoBo<?> fileBo) {
        try {
            getClient(fileBo).deleteObject(getBucketName(fileBo), getFilePath(fileBo));
            return true;
        } catch (OSSException e) {
            log.error("OSS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileDeleteException("文件删除失败，请稍后重试", e);
        } catch (ClientException e) {
            log.error("OSS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(fileBo), e.getMessage(), e);
            throw new FileDeleteException("文件删除失败，请稍后重试", e);
        } catch (Exception e) {
            log.error("文件删除异常，文件路径: {}", getFilePath(fileBo), e);
            throw new FileDeleteException("文件删除失败，请稍后重试", e);
        }
    }
}

