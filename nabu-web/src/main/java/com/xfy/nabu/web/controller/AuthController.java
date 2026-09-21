package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.auth.dto.LoginRequestDTO;
import com.xfy.nabu.api.auth.dto.TokenPairDTO;
import com.xfy.nabu.api.auth.service.AuthService;
import com.xfy.nabu.api.user.dto.UserRegisterDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.web.dto.RefreshTokenRequest;
import com.xfy.nabu.web.dto.RegisterRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录 / 刷新 token / 注册 / 登出。
 *
 * <p>登录、注册、刷新是公开接口（在 {@link com.xfy.nabu.web.config.WebMvcConfig} 里排除了 JWT 拦截），
 * 登出需要携带 Authorization 头。</p>
 */
@RestController
public class AuthController {

    @DubboReference
    private AuthService authService;

    @DubboReference
    private UserService userService;

    private final PasswordEncoder passwordEncoder;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 登录，返回 access/refresh token 对。
     */
    @PostMapping("/api/v1/auth/login")
    public Result<TokenPairDTO> login(@RequestBody LoginRequestDTO request) {
        TokenPairDTO tokenPair = authService.login(request.getUsername(), request.getPassword());
        return Result.success(tokenPair);
    }

    /**
     * 使用 refresh token 换取新的 access token。
     */
    @PostMapping("/api/v1/auth/refresh")
    public Result<TokenPairDTO> refresh(@RequestBody RefreshTokenRequest request) {
        TokenPairDTO tokenPair = authService.refresh(request.getRefreshToken());
        return Result.success(tokenPair);
    }

    /**
     * 用户注册。
     *
     * <p>nabu-web 需要先对明文密码做 BCrypt 哈希，再通过 {@code @DubboReference}
     * 调用 {@link UserService#register(UserRegisterDTO)} 传入哈希后的密码，
     * 密码哈希的计算不下沉到 user-service，避免明文密码经 Dubbo 传输。</p>
     */
    @PostMapping("/api/v1/auth/register")
    public Result<Long> register(@RequestBody RegisterRequest request) {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(request.getUsername());
        registerDTO.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        registerDTO.setNickname(request.getNickname());
        Long newUserId = userService.register(registerDTO);
        return Result.success(newUserId);
    }

    /**
     * 登出：从 Authorization 头取 access token 并使其失效。
     */
    @PostMapping("/api/v1/auth/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success();
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
