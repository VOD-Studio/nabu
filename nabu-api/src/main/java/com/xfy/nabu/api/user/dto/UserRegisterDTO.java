package com.xfy.nabu.api.user.dto;

import java.io.Serializable;

/**
 * 用户注册请求参数（auth-service 校验通过密码后，通过 Dubbo 调用 user-service 落库）。
 */
public class UserRegisterDTO implements Serializable {

    private String username;
    private String passwordHash;
    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
