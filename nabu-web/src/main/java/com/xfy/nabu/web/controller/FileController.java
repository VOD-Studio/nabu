package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.file.dto.FileMetaDTO;
import com.xfy.nabu.api.file.dto.PresignedUploadDTO;
import com.xfy.nabu.api.file.service.FileService;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.util.TraceContext;
import com.xfy.nabu.web.dto.ConfirmUploadRequest;
import com.xfy.nabu.web.dto.PresignUploadRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件上传接口：生成预签名直传地址 / 确认直传完成。均为薄转发层，调用 {@link FileService} 完成实际业务逻辑，
 * 上传人 id 取自 {@link TraceContext#getUserId()}（由 JWT 拦截器写入）。
 */
@RestController
public class FileController {

    @DubboReference
    private FileService fileService;

    /**
     * 生成预签名 PUT URL，供前端直传 RustFS，不经过 Java 服务中转大文件。
     */
    @PostMapping("/api/v1/files/presign")
    public Result<PresignedUploadDTO> presign(@RequestBody PresignUploadRequest request) {
        Long uploaderId = TraceContext.getUserId();
        PresignedUploadDTO presignedUploadDTO = fileService.createPresignedUpload(
                uploaderId, request.getOriginalName(), request.getContentType());
        return Result.success(presignedUploadDTO);
    }

    /**
     * 直传完成后，前端回调此接口落库文件元数据。
     */
    @PostMapping("/api/v1/files/confirm")
    public Result<FileMetaDTO> confirm(@RequestBody ConfirmUploadRequest request) {
        Long uploaderId = TraceContext.getUserId();
        FileMetaDTO fileMetaDTO = fileService.confirmUpload(
                uploaderId, request.getObjectKey(), request.getOriginalName(),
                request.getContentType(), request.getSizeBytes());
        return Result.success(fileMetaDTO);
    }
}
