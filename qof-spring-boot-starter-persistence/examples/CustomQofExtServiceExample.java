package io.github.codeyunze.examples.persistence;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.FilesService;
import io.github.codeyunze.service.QofExtService;
import io.github.codeyunze.service.impl.AbstractQofServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 【案例】自定义元数据扩展：在默认落库逻辑上叠加审计 / 业务校验。
 * <p>
 * 引入 {@code qof-spring-boot-starter-persistence} 后，自动配置已提供基于 DB 的
 * {@link QofExtService}。若宿主需要额外逻辑，可继承 {@link AbstractQofServiceImpl}
 * 并声明为 Bean（建议 {@code @Primary}），以覆盖自动配置中的默认实现。
 * <p>
 * 注意：本文件仅供参考，不会被 starter 编译打包；请复制到宿主工程后去掉本注释再使用。
 */
@Primary
@Service
public class CustomQofExtServiceExample extends AbstractQofServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(CustomQofExtServiceExample.class);

    public CustomQofExtServiceExample(FilesService filesService) {
        super(filesService);
    }

    @Override
    public Long beforeUpload(QofFileInfoDto<?> fileDto) {
        // 例：强制要求创建者，便于后续私有文件鉴权
        if (fileDto.getCreateId() == null) {
            throw new IllegalArgumentException("上传私有文件时 createId 不能为空");
        }
        log.info("上传前审计: name={}, mode={}, createId={}",
                fileDto.getFileName(), fileDto.getFileStorageMode(), fileDto.getCreateId());
        return super.beforeUpload(fileDto);
    }

    @Override
    public QofFileInfoBo<?> afterUpload(QofFileInfoDto<?> fileDto) {
        // 先走父类落库（受 qof.persistent-enable 控制），再做业务侧关联
        QofFileInfoBo<?> saved = super.afterUpload(fileDto);
        log.info("上传后落库完成: fileId={}", saved.getFileId());
        return saved;
    }

    @Override
    public void beforeDownload(QofFileInfoBo<?> fileBo) {
        log.debug("下载审计: fileId={}, path={}", fileBo.getFileId(), fileBo.getFilePath());
        super.beforeDownload(fileBo);
    }
}
