package com.xfy.nabu.auth.service;

import com.xfy.nabu.api.auth.dto.TokenPairDTO;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code AuthService}），不直接依赖 Dubbo 注解，
 * 供 {@link com.xfy.nabu.auth.dubbo.AuthServiceDubboImpl} 转发调用。
 */
public interface AuthBizService {

    TokenPairDTO login(String username, String password);

    TokenPairDTO refresh(String refreshToken);

    Long verifyToken(String accessToken);

    void logout(String accessToken);
}
