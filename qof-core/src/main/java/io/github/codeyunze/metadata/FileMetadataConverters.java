package io.github.codeyunze.metadata;

import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.spi.metadata.FileMetadata;
import org.springframework.beans.BeanUtils;

/**
 * {@link FileMetadata} 与 core DTO/BO 互转。
 *
 * @author yunze
 * @since 18.0.0
 */
public final class FileMetadataConverters {

    private FileMetadataConverters() {
    }

    public static FileMetadata fromDto(QofFileInfoDto<?> dto) {
        FileMetadata metadata = new FileMetadata();
        if (dto == null) {
            return metadata;
        }
        metadata.setFileId(dto.getFileId());
        metadata.setFileName(dto.getFileName());
        metadata.setFilePath(dto.getFilePath());
        metadata.setFileType(dto.getFileType());
        metadata.setFileLabel(dto.getFileLabel());
        metadata.setFileSize(dto.getFileSize());
        metadata.setFileStorageMode(dto.getFileStorageMode());
        metadata.setFileStorageStation(dto.getFileStorageStation());
        metadata.setPublicAccess(dto.getPublicAccess());
        metadata.setCreateId(dto.getCreateId());
        return metadata;
    }

    public static FileMetadata fromBo(QofFileInfoBo<?> bo) {
        FileMetadata metadata = new FileMetadata();
        if (bo == null) {
            return metadata;
        }
        BeanUtils.copyProperties(bo, metadata);
        return metadata;
    }

    public static QofFileInfoBo<?> toBo(FileMetadata metadata) {
        QofFileInfoBo<?> bo = new QofFileInfoBo<>();
        if (metadata == null) {
            return bo;
        }
        BeanUtils.copyProperties(metadata, bo);
        return bo;
    }
}
