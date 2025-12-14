package io.github.codeyunze.service.impl;

import io.github.codeyunze.service.SysFilesService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * QOF文件信息操作扩展接口默认实现
 * 当用户没有提供自定义实现时，使用此默认实现
 * 
 * 注意：此实现会覆盖qof-core-slim模块中的默认实现
 *
 * @author 高晗
 * @since 2025/2/18 07:49
 */
@Primary
@Service
public class DefaultQofServiceImpl extends AbstractQofServiceImpl {

    public DefaultQofServiceImpl(SysFilesService filesService) {
        super(filesService);
    }
}
