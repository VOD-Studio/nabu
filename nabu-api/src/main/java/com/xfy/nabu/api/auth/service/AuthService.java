package com.xfy.nabu.api.auth.service;

import com.xfy.nabu.api.auth.dto.TokenPairDTO;

/**
 * 认证服务 Dubbo 接口，供 nabu-web 内部调用（HTTP 登录接口最终转发到这里）。
 */
public interface AuthService {

    TokenPairDTO login(String username, String password);

    TokenPairDTO refresh(String refreshToken);

    /** 校验 access token，返回其中的 userId；无效则返回 null */
    Long verifyToken(String accessToken);

    void logout(String accessToken);
}
