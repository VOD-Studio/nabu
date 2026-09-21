package com.xfy.nabu.api.file.dto;

import java.io.Serializable;

/**
 * 预签名直传地址：客户端拿到该 URL 后可直接 PUT 到 RustFS，绕过 file-service 中转大文件。
 */
public class PresignedUploadDTO implements Serializable {

    private String uploadUrl;
    private String objectKey;
    private long expiresInSeconds;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
