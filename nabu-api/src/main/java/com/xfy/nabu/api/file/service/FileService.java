package com.xfy.nabu.api.file.service;

import com.xfy.nabu.api.file.dto.FileMetaDTO;
import com.xfy.nabu.api.file.dto.PresignedUploadDTO;

/**
 * 文件服务 Dubbo 接口，由 nabu-file-service 提供实现，内部通过 AWS S3 SDK 对接 RustFS。
 */
public interface FileService {

    /** 生成预签名 PUT URL，供前端直传 RustFS，不经过 Java 服务中转大文件 */
    PresignedUploadDTO createPresignedUpload(Long uploaderId, String originalName, String contentType);

    /** 直传完成后，前端回调此接口落库文件元数据 */
    FileMetaDTO confirmUpload(Long uploaderId, String objectKey, String originalName, String contentType, long sizeBytes);

    FileMetaDTO getById(Long fileId);

    void delete(Long fileId, Long operatorUserId);
}
