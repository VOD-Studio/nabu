package com.xfy.nabu.file.dubbo;

import com.xfy.nabu.api.file.dto.FileMetaDTO;
import com.xfy.nabu.api.file.dto.PresignedUploadDTO;
import com.xfy.nabu.api.file.service.FileService;
import com.xfy.nabu.file.service.FileBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link FileService}，内部转发给 {@link FileBizService}。
 */
@DubboService
public class FileServiceDubboImpl implements FileService {

    private final FileBizService fileBizService;

    public FileServiceDubboImpl(FileBizService fileBizService) {
        this.fileBizService = fileBizService;
    }

    @Override
    public PresignedUploadDTO createPresignedUpload(Long uploaderId, String originalName, String contentType) {
        return fileBizService.createPresignedUpload(uploaderId, originalName, contentType);
    }

    @Override
    public FileMetaDTO confirmUpload(Long uploaderId, String objectKey, String originalName, String contentType, long sizeBytes) {
        return fileBizService.confirmUpload(uploaderId, objectKey, originalName, contentType, sizeBytes);
    }

    @Override
    public FileMetaDTO getById(Long fileId) {
        return fileBizService.getById(fileId);
    }

    @Override
    public void delete(Long fileId, Long operatorUserId) {
        fileBizService.delete(fileId, operatorUserId);
    }
}
