package io.github.codeyunze.core.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.core.AbstractQofClient;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.QofExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云文件操作接口实现
 *
 * @author yunze
 * @since 2025/2/17 16:53
 */
@Service
public class CosQofClient extends AbstractQofClient implements QofClient {

    private static final Logger log = LoggerFactory.getLogger(CosQofClient.class);

    @Resource
    private CosQofProperties fileProperties;

    @Resource
    private COSClient cosClient;

    public CosQofClient(QofExtService qofExtService) {
        super(qofExtService);
    }

    @Override
    protected Long doUpload(InputStream fis, QofFileInfoDto info) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(info.getFileSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(fileProperties.getBucketName(), fileProperties.getFilepath() + info.getFilePath(), fis, objectMetadata);
        // 设置单链接限速（如有需要），不需要可忽略
        putObjectRequest.setTrafficLimit(8 * 1024 * 1024);
        try {
            cosClient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            log.error("COS服务异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败，服务异常", e);
        } catch (CosClientException e) {
            log.error("COS客户端异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败，客户端异常", e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return info.getFileId();
    }

    @Override
    protected QofFileDownloadBo doDownload(QofFileInfoBo fileBo) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(fileProperties.getBucketName(), fileProperties.getFilepath() + fileBo.getFilePath());
        COSObject cosObject = cosClient.getObject(getObjectRequest);

        QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
        BeanUtils.copyProperties(fileBo, fileDownloadBo);
        fileDownloadBo.setInputStream(cosObject.getObjectContent());
        return fileDownloadBo;
    }

    @Override
    protected boolean doDelete(QofFileInfoBo fileBo) {
        String filePath = fileProperties.getFilepath() + fileBo.getFilePath();
        cosClient.deleteObject(fileProperties.getBucketName(), filePath);
        return true;
    }
}
