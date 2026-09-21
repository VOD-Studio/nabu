package com.xfy.nabu.web.dto;

/**
 * 请求生成预签名直传地址的请求体。
 */
public class PresignUploadRequest {

    private String originalName;
    private String contentType;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
