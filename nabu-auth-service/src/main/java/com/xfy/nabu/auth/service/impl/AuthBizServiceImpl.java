package com.xfy.nabu.auth.service.impl;

import com.xfy.nabu.api.auth.dto.TokenPairDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.auth.service.AuthBizService;
import com.xfy.nabu.auth.service.JwtTokenProvider;
import com.xfy.nabu.common.constant.CommonConstants;
import com.xfy.nabu.common.exception.BusinessException;
import com.xfy.nabu.common.result.ResultCode;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthBizServiceImpl implements AuthBizService {

    /** Redis key 前缀："nabu:auth:refresh:{userId}:{jti}"，value 为 refresh token 本身，用于登出时校验+删除 */
    private static final String REFRESH_KEY_PREFIX = CommonConstants.REDIS_KEY_PREFIX + "auth:refresh:";

    @DubboReference
    private UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public AuthBizServiceImpl(
            PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, StringRedisTemplate redisTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenPairDTO login(String username, String password) {
        String passwordHash = userService.getPasswordHashByUsername(username);
        if (passwordHash == null || !passwordEncoder.matches(password, passwordHash)) {
            throw new BusinessException(ResultCode.PARAM_INVALID.getCode(), "用户名或密码错误");
        }
        Long userId = userService.getByUsername(username).getId();
        return issueTokenPair(userId);
    }

    @Override
    public TokenPairDTO refresh(String refreshToken) {
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        if (claims == null || !"refresh".equals(claims.get("type"))) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "refresh token 无效或已过期");
        }
        Long userId = jwtTokenProvider.getUserId(claims);
        String jti = claims.getId();
        String redisKey = refreshKey(userId, jti);
        String stored = redisTemplate.opsForValue().get(redisKey);
        if (stored == null || !stored.equals(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "refresh token 已失效，请重新登录");
        }
        // 续期策略：旧 refresh token 失效，签发新的 access + refresh token（refresh token 轮换，降低被盗用风险）。
        redisTemplate.delete(redisKey);
        return issueTokenPair(userId);
    }

    @Override
    public Long verifyToken(String accessToken) {
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        if (claims == null || !"access".equals(claims.get("type"))) {
            return null;
        }
        return jwtTokenProvider.getUserId(claims);
    }

    @Override
    public void logout(String accessToken) {
        // access token 是无状态 JWT，服务端不维护黑名单（简化处理，注释说明生产可选方案）。
        // 生产环境如需要"立即失效"能力，可维护一个基于 jti 的黑名单（Redis + TTL=剩余有效期），
        // 这里作为演示，登出时仅清理该用户名下已知的 refresh token（如果调用方一并传了 userId 会更精确，
        // 当前 AuthService 契约只接收 accessToken，所以从 accessToken 里解析出 userId 后按前缀扫描清理）。
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        if (claims == null) {
            return;
        }
        Long userId = jwtTokenProvider.getUserId(claims);
        if (userId != null) {
            redisTemplate.delete(redisTemplate.keys(REFRESH_KEY_PREFIX + userId + ":*"));
        }
    }

    private TokenPairDTO issueTokenPair(Long userId) {
        String accessJti = JwtTokenProvider.newJti();
        String refreshJti = JwtTokenProvider.newJti();
        String accessToken = jwtTokenProvider.generateAccessToken(userId, accessJti);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, refreshJti);

        redisTemplate
                .opsForValue()
                .set(
                        refreshKey(userId, refreshJti),
                        refreshToken,
                        Duration.ofSeconds(jwtTokenProvider.refreshTokenExpireSeconds()));

        return new TokenPairDTO(accessToken, refreshToken, jwtTokenProvider.accessTokenExpireSeconds());
    }

    private String refreshKey(Long userId, String jti) {
        return REFRESH_KEY_PREFIX + userId + ":" + jti;
    }
}
