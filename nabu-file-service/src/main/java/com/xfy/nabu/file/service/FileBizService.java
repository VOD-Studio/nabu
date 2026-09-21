package com.xfy.nabu.file.service;

import com.xfy.nabu.api.file.dto.FileMetaDTO;
import com.xfy.nabu.api.file.dto.PresignedUploadDTO;

/**
 * 内部业务服务接口，供 {@link com.xfy.nabu.file.dubbo.FileServiceDubboImpl} 转发。
 */
public interface FileBizService {

    PresignedUploadDTO createPresignedUpload(Long uploaderId, String originalName, String contentType);

    FileMetaDTO confirmUpload(
            Long uploaderId, String objectKey, String originalName, String contentType, long sizeBytes);

    FileMetaDTO getById(Long fileId);

    void delete(Long fileId, Long operatorUserId);
}
