package com.xfy.nabu.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 相关配置，对应 application.yml 的 nabu.jwt.* 前缀。
 * 生产环境务必：1) secret 换成足够长度的随机密钥，并从 Nacos 配置中心 / 环境变量注入，不要硬编码在代码或明文 yml 里；
 * 2) 定期轮换密钥（配合 kid 多密钥校验）。
 */
@ConfigurationProperties(prefix = "nabu.jwt")
public class JwtProperties {

    /** 签名密钥，默认值仅用于本地开发调试 */
    private String secret = "nabu-learning-project-jwt-secret-key-please-change-me-in-prod-0123456789";
    /** access token 有效期（分钟） */
    private long accessTokenExpireMinutes = 30;
    /** refresh token 有效期（天） */
    private long refreshTokenExpireDays = 7;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpireMinutes() {
        return accessTokenExpireMinutes;
    }

    public void setAccessTokenExpireMinutes(long accessTokenExpireMinutes) {
        this.accessTokenExpireMinutes = accessTokenExpireMinutes;
    }

    public long getRefreshTokenExpireDays() {
        return refreshTokenExpireDays;
    }

    public void setRefreshTokenExpireDays(long refreshTokenExpireDays) {
        this.refreshTokenExpireDays = refreshTokenExpireDays;
    }
}
