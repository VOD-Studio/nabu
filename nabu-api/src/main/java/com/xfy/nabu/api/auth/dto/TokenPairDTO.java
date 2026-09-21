package com.xfy.nabu.api.auth.dto;

import java.io.Serializable;

/**
 * JWT 双 token 返回结构：access token 短期有效，refresh token 用于续期。
 */
public class TokenPairDTO implements Serializable {

    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;

    public TokenPairDTO() {}

    public TokenPairDTO(String accessToken, String refreshToken, long accessTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getAccessTokenExpiresIn() {
        return accessTokenExpiresIn;
    }

    public void setAccessTokenExpiresIn(long accessTokenExpiresIn) {
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
