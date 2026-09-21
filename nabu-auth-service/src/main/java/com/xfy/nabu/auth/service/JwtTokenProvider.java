package com.xfy.nabu.auth.service;

import com.xfy.nabu.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * JWT 签发/解析工具类，基于 jjwt 0.12.x API。
 * access token / refresh token 都携带 userId（subject）与随机 jti（用于 Redis 侧的 refresh token 管理）。
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // HS256 要求密钥长度 >= 256 bit，默认密钥已满足长度要求。
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(jwtProperties.getAccessTokenExpireMinutes()))))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofDays(jwtProperties.getRefreshTokenExpireDays()))))
                .signWith(secretKey)
                .compact();
    }

    public long accessTokenExpireSeconds() {
        return Duration.ofMinutes(jwtProperties.getAccessTokenExpireMinutes()).toSeconds();
    }

    public long refreshTokenExpireSeconds() {
        return Duration.ofDays(jwtProperties.getRefreshTokenExpireDays()).toSeconds();
    }

    public static String newJti() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 解析并校验签名/有效期，失败返回 null（不抛异常打断调用方主流程） */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
