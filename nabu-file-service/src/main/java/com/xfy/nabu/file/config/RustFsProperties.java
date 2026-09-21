package com.xfy.nabu.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RustFS 连接配置，对应 application.yml 的 nabu.rustfs.* 前缀。
 */
@ConfigurationProperties(prefix = "nabu.rustfs")
public class RustFsProperties {

    /** S3 兼容 API 地址 */
    private String endpoint = "http://127.0.0.1:9000";
    private String accessKey = "rustfsadmin";
    private String secretKey = "rustfsadmin";
    private String bucket = "nabu-files";
    /** RustFS 不校验真实 region，这里只是 AWS SDK 客户端构建时的必填占位值 */
    private String region = "us-east-1";
    /** 预签名 URL 有效期（分钟） */
    private int presignExpireMinutes = 15;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPresignExpireMinutes() {
        return presignExpireMinutes;
    }

    public void setPresignExpireMinutes(int presignExpireMinutes) {
        this.presignExpireMinutes = presignExpireMinutes;
    }
}
