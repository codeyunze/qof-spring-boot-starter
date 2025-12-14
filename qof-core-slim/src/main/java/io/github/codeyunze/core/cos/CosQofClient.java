package io.github.codeyunze.core.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import io.github.codeyunze.QofConstant;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofFileOperationBase;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 腾讯云文件操作接口实现
 *
 * @author 高晗
 * @since 2025/2/17 16:53
 */
@Service
@ConditionalOnProperty(prefix = "qof.cos", name = QofConstant.ENABLE, havingValue = QofConstant.ENABLE_VALUE)
public class CosQofClient extends AbstractQofClient {

    private static final Logger log = LoggerFactory.getLogger(CosQofClient.class);

    @Resource
    private CosQofProperties fileProperties;

    @Resource
    private Map<String, COSClient> cosClientMap;

    public CosQofClient(QofExtService qofExtService) {
        super(qofExtService);
    }

    private COSClient getClient(QofFileOperationBase fileOperationBase) {
        String fileStorageStation;
        Map<String, CosQofConfig> multiple = fileProperties.getMultiple();
        if (!CollectionUtils.isEmpty(multiple) && multiple.containsKey(fileOperationBase.getFileStorageStation())) {
            fileStorageStation = fileOperationBase.getFileStorageStation();
        } else {
            fileStorageStation = fileProperties.getDefaultStorageStation();
        }
        String clientKey = fileStorageStation + StrUtils.toUpperCase(QofConstant.StorageMode.COS);
        COSClient client = cosClientMap.get(clientKey);
        if (client == null) {
            throw new IllegalStateException("未找到COS客户端，存储站: " + fileStorageStation);
        }
        return client;
    }

    private String getBucketName(QofFileOperationBase fileOperationBase) {
        Map<String, CosQofConfig> multiple = fileProperties.getMultiple();
        String fileStorageStation;
        if (CollectionUtils.isEmpty(multiple) || !multiple.containsKey(fileOperationBase.getFileStorageStation())) {
            fileStorageStation = fileProperties.getDefaultStorageStation();
        } else {
            fileStorageStation = fileOperationBase.getFileStorageStation();
        }
        
        // 如果multiple为空，使用父类配置
        if (CollectionUtils.isEmpty(multiple)) {
            return fileProperties.getBucketName();
        }
        
        CosQofConfig config = multiple.get(fileStorageStation);
        if (config == null) {
            throw new IllegalStateException("未找到存储站配置: " + fileStorageStation);
        }
        return config.getBucketName();
    }

    private String getFilePath(QofFileOperationBase fileOperationBase) {
        Map<String, CosQofConfig> multiple = fileProperties.getMultiple();
        String fileStorageStation;
        if (CollectionUtils.isEmpty(multiple) || !multiple.containsKey(fileOperationBase.getFileStorageStation())) {
            fileStorageStation = fileProperties.getDefaultStorageStation();
        } else {
            fileStorageStation = fileOperationBase.getFileStorageStation();
        }
        
        String filepath;
        // 如果multiple为空，使用父类配置
        if (CollectionUtils.isEmpty(multiple)) {
            filepath = fileProperties.getFilepath();
        } else {
            CosQofConfig config = multiple.get(fileStorageStation);
            if (config == null) {
                throw new IllegalStateException("未找到存储站配置: " + fileStorageStation);
            }
            filepath = config.getFilepath();
        }
        
        return filepath + fileOperationBase.getFilePath();
    }

    private Long getTrafficLimit(QofFileOperationBase fileOperationBase) {
        Map<String, CosQofConfig> multiple = fileProperties.getMultiple();
        String fileStorageStation;
        if (CollectionUtils.isEmpty(multiple) || !multiple.containsKey(fileOperationBase.getFileStorageStation())) {
            fileStorageStation = fileProperties.getDefaultStorageStation();
        } else {
            fileStorageStation = fileOperationBase.getFileStorageStation();
        }
        
        // 如果multiple为空，使用父类配置
        if (CollectionUtils.isEmpty(multiple)) {
            return fileProperties.getTrafficLimit();
        }
        
        CosQofConfig config = multiple.get(fileStorageStation);
        if (config == null) {
            return fileProperties.getTrafficLimit();
        }
        return config.getTrafficLimit();
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto<?> info) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(info.getFileSize());
        // 设置单链接限速（如有需要），不需要可忽略
        try (InputStream inputStream = fis) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(info), getFilePath(info), inputStream, objectMetadata);
            Long trafficLimit = getTrafficLimit(info);
            if (trafficLimit != null && trafficLimit > 0) {
                putObjectRequest.setTrafficLimit(trafficLimit.intValue());
            }
            COSClient client = getClient(info);
            client.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            log.error("COS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(info), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileUploadException("文件上传失败，COS服务异常: " + e.getErrorMessage(), e);
        } catch (CosClientException e) {
            log.error("COS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(info), e.getMessage(), e);
            throw new FileUploadException("文件上传失败，COS客户端异常: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("文件流处理异常，文件路径: {}", getFilePath(info), e);
            throw new FileUploadException("文件上传失败，流处理异常", e);
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo<?> fileBo) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(getBucketName(fileBo), getFilePath(fileBo));
            COSObject cosObject = getClient(fileBo).getObject(getObjectRequest);

            QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
            BeanUtils.copyProperties(fileBo, fileDownloadBo);
            fileDownloadBo.setInputStream(cosObject.getObjectContent());
            return fileDownloadBo;
        } catch (CosServiceException e) {
            log.error("COS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileDownloadException("文件下载失败，COS服务异常: " + e.getErrorMessage(), e);
        } catch (CosClientException e) {
            log.error("COS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(fileBo), e.getMessage(), e);
            throw new FileDownloadException("文件下载失败，COS客户端异常: " + e.getMessage(), e);
        }
    }

    @Override
    protected boolean doDelete(QofFileInfoBo<?> fileBo) {
        try {
            getClient(fileBo).deleteObject(getBucketName(fileBo), getFilePath(fileBo));
            return true;
        } catch (CosServiceException e) {
            log.error("COS服务异常，文件路径: {}, 错误码: {}, 错误信息: {}", getFilePath(fileBo), e.getErrorCode(), e.getErrorMessage(), e);
            throw new FileDeleteException("文件删除失败，COS服务异常: " + e.getErrorMessage(), e);
        } catch (CosClientException e) {
            log.error("COS客户端异常，文件路径: {}, 异常信息: {}", getFilePath(fileBo), e.getMessage(), e);
            throw new FileDeleteException("文件删除失败，COS客户端异常: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("文件删除异常，文件路径: {}", getFilePath(fileBo), e);
            throw new FileDeleteException("文件删除失败: " + e.getMessage(), e);
        }
    }
}
