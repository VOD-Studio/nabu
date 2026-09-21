package com.xfy.nabu.auth.dubbo;

import com.xfy.nabu.api.auth.dto.TokenPairDTO;
import com.xfy.nabu.api.auth.service.AuthService;
import com.xfy.nabu.auth.service.AuthBizService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo Provider：对外暴露 {@link AuthService}，内部转发给 {@link AuthBizService}。
 */
@DubboService
public class AuthServiceDubboImpl implements AuthService {

    private final AuthBizService authBizService;

    public AuthServiceDubboImpl(AuthBizService authBizService) {
        this.authBizService = authBizService;
    }

    @Override
    public TokenPairDTO login(String username, String password) {
        return authBizService.login(username, password);
    }

    @Override
    public TokenPairDTO refresh(String refreshToken) {
        return authBizService.refresh(refreshToken);
    }

    @Override
    public Long verifyToken(String accessToken) {
        return authBizService.verifyToken(accessToken);
    }

    @Override
    public void logout(String accessToken) {
        authBizService.logout(accessToken);
    }
}
