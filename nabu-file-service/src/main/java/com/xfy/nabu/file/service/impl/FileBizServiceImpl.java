package com.xfy.nabu.file.service.impl;

import com.xfy.nabu.api.file.dto.FileMetaDTO;
import com.xfy.nabu.api.file.dto.PresignedUploadDTO;
import com.xfy.nabu.common.exception.BusinessException;
import com.xfy.nabu.common.result.ResultCode;
import com.xfy.nabu.file.config.RustFsProperties;
import com.xfy.nabu.file.domain.FileMetaEntity;
import com.xfy.nabu.file.mapper.FileMetaMapper;
import com.xfy.nabu.file.service.FileBizService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileBizServiceImpl implements FileBizService {

    private final S3Presigner s3Presigner;
    private final RustFsProperties rustFsProperties;
    private final FileMetaMapper fileMetaMapper;

    public FileBizServiceImpl(S3Presigner s3Presigner,
                               RustFsProperties rustFsProperties,
                               FileMetaMapper fileMetaMapper) {
        this.s3Presigner = s3Presigner;
        this.rustFsProperties = rustFsProperties;
        this.fileMetaMapper = fileMetaMapper;
    }

    @Override
    public PresignedUploadDTO createPresignedUpload(Long uploaderId, String originalName, String contentType) {
        String objectKey = buildObjectKey(originalName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(rustFsProperties.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(rustFsProperties.getPresignExpireMinutes()))
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        PresignedUploadDTO dto = new PresignedUploadDTO();
        dto.setUploadUrl(presigned.url().toString());
        dto.setObjectKey(objectKey);
        dto.setExpiresInSeconds(Duration.ofMinutes(rustFsProperties.getPresignExpireMinutes()).getSeconds());
        return dto;
    }

    @Override
    public FileMetaDTO confirmUpload(Long uploaderId, String objectKey, String originalName, String contentType, long sizeBytes) {
        // 简化处理：不做真实大小校验（生产环境建议先 HeadObject 核对一次实际对象大小，避免元数据与真实文件不一致）。
        FileMetaEntity entity = new FileMetaEntity();
        entity.setUploaderId(uploaderId);
        entity.setBucket(rustFsProperties.getBucket());
        entity.setObjectKey(objectKey);
        entity.setOriginalName(originalName);
        entity.setContentType(contentType);
        entity.setSizeBytes(sizeBytes);
        // 生产环境此处应该走 CDN 域名或网关统一代理，而不是直接暴露对象存储服务的 endpoint。
        entity.setUrl(rustFsProperties.getEndpoint() + "/" + rustFsProperties.getBucket() + "/" + objectKey);
        fileMetaMapper.insert(entity);
        return toDTO(entity);
    }

    @Override
    public FileMetaDTO getById(Long fileId) {
        FileMetaEntity entity = fileMetaMapper.selectById(fileId);
        return entity == null ? null : toDTO(entity);
    }

    @Override
    public void delete(Long fileId, Long operatorUserId) {
        FileMetaEntity entity = fileMetaMapper.selectById(fileId);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!entity.getUploaderId().equals(operatorUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // 简化处理：仅删除数据库元数据，未同步删除 RustFS 上的真实对象。
        // 生产环境应在此处调用 S3Client.deleteObject(...)，或交由 RustFS Lifecycle 规则统一清理孤儿对象。
        fileMetaMapper.deleteById(fileId);
    }

    /**
     * 生成 objectKey：{@code yyyy/MM/dd/uuid-originalName}，按日期分区便于后续按前缀批量清理/统计，
     * 也天然打散写入压力，避免单个前缀下对象过多。
     */
    private String buildObjectKey(String originalName) {
        LocalDate today = LocalDate.now();
        String safeName = originalName == null ? "file" : originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        return String.format("%d/%02d/%02d/%s-%s",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
                UUID.randomUUID().toString().replace("-", ""), safeName);
    }

    private FileMetaDTO toDTO(FileMetaEntity entity) {
        FileMetaDTO dto = new FileMetaDTO();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setBucket(entity.getBucket());
        dto.setObjectKey(entity.getObjectKey());
        dto.setOriginalName(entity.getOriginalName());
        dto.setContentType(entity.getContentType());
        dto.setSizeBytes(entity.getSizeBytes() == null ? 0 : entity.getSizeBytes());
        dto.setUrl(entity.getUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
