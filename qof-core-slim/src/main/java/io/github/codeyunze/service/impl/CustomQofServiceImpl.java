package io.github.codeyunze.service.impl;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.SysFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * QOF文件信息操作扩展接口自定义实现
 *
 * @author yunze
 * @since 2025/2/18 07:49
 */
// @Primary
// @Service
public class CustomQofServiceImpl extends AbstractQofServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(CustomQofServiceImpl.class);

    public CustomQofServiceImpl(SysFilesService filesService) {
        super(filesService);
    }

    @Override
    public Long beforeUpload(QofFileInfoDto<?> fileDto) {
        log.info("自定义-文件上传前执行");
        return super.beforeUpload(fileDto);
    }

    @Override
    public boolean afterDelete(QofFileInfoBo<?> fileBo) {
        log.info("自定义-文件删除后执行");
        return false;
    }
}
