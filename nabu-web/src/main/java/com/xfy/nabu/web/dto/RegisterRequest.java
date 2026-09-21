package com.xfy.nabu.web.dto;

/**
 * 用户注册请求体。密码在 {@link com.xfy.nabu.web.controller.AuthController} 里
 * 用 {@code BCryptPasswordEncoder} 哈希后，再转换为
 * {@link com.xfy.nabu.api.user.dto.UserRegisterDTO} 通过 Dubbo 调用 UserService.register。
 */
public class RegisterRequest {

    private String username;
    private String password;
    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
