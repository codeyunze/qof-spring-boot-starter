package io.github.codeyunze.examples.persistence;

import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.bo.SysFilesMetaBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.FilesService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 【案例】宿主自建门面：只引 persistence，不引 starter-web 时，自行封装上传/下载/列表。
 * <p>
 * 核心协作关系：
 * <ul>
 *   <li>{@link QofClientFactory} / {@link QofClient}：对象存储读写</li>
 *   <li>{@link FilesService}：sys_files 元数据 CRUD / 分页 / 访问鉴权</li>
 *   <li>持久化钩子由 starter 注入的 {@code QofExtService} 在 Client 内部自动调用</li>
 * </ul>
 * 本文件仅供参考，不会被 starter 编译打包。
 */
@Service
public class HostFileFacadeExample {

    private final QofClientFactory qofClientFactory;
    private final FilesService filesService;

    public HostFileFacadeExample(QofClientFactory qofClientFactory, FilesService filesService) {
        this.qofClientFactory = qofClientFactory;
        this.filesService = filesService;
    }

    /**
     * 上传：Client.upload 成功后会回调 QofExtService.afterUpload → FilesService.save
     */
    public Long upload(MultipartFile file, String storageMode, String station, Long createId) throws Exception {
        QofFileInfoDto<?> info = new QofFileInfoDto<>();
        info.setFileName(file.getOriginalFilename());
        info.setFileType(file.getContentType());
        info.setFileSize(file.getSize());
        info.setFileStorageMode(storageMode);
        info.setFileStorageStation(station);
        // 业务子目录（相对存储站 filepath），建议按年月隔离，避免路径遍历由调用方硬拼绝对路径
        info.setDirectoryAddress("/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
        info.setPublicAccess(0);
        info.setCreateId(createId);

        QofClient client = qofClientFactory.buildClient(storageMode);
        try (InputStream in = file.getInputStream()) {
            return client.upload(in, info);
        }
    }

    /**
     * 按 fileId 下载：依赖库中元数据拿到 path / mode / station
     */
    public QofFileDownloadBo download(Long fileId, String storageMode, Long createId) {
        filesService.checkFileAccessPermission(fileId, createId);
        return qofClientFactory.buildClient(storageMode).download(fileId);
    }

    /**
     * 仅查元数据列表（不含文件流）
     */
    public IPage<SysFilesMetaBo> page(long pageNum, long pageSize, String fileName) {
        return filesService.pageFiles(new Page<>(pageNum, pageSize), fileName, null, null);
    }

    /**
     * 按 fileId 查单条元数据
     */
    public QofFileInfoBo<?> meta(Long fileId) {
        return filesService.getByFileId(fileId);
    }

    /**
     * 删除：Client.delete → QofExtService.beforeDelete → FilesService.deleteByFileId
     */
    public boolean delete(Long fileId, String storageMode) {
        return qofClientFactory.buildClient(storageMode).delete(fileId);
    }
}
