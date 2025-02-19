package io.github.qof.service.impl;

import io.github.qof.dto.QofFileInfoDto;
import io.github.qof.service.SysFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * QOF文件信息操作扩展接口自定义实现
 *
 * @author yunze
 * @date 2025/2/18 07:49
 */
@Slf4j
@Primary
@Service
public class CustomQofServiceImpl extends AbstractQofServiceImpl {

    public CustomQofServiceImpl(SysFilesService filesService) {
        super(filesService);
    }

    @Override
    public Long beforeUpload(QofFileInfoDto fileDto) {
        log.info("自定义-文件上传前执行");
        return super.beforeUpload(fileDto);
    }


}
