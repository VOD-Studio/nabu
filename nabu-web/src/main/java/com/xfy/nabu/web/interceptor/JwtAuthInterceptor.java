package com.xfy.nabu.web.interceptor;

import com.alibaba.fastjson2.JSON;
import com.xfy.nabu.api.auth.service.AuthService;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.result.ResultCode;
import com.xfy.nabu.common.util.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器。
 *
 * <p>从请求头 {@code Authorization: Bearer {token}} 中取出 access token，
 * 通过 {@code @DubboReference} 调用 {@link AuthService#verifyToken(String)} 校验：
 * 校验通过则把 userId 写入 {@link TraceContext}，供后续 Controller / Dubbo 调用链路读取；
 * 校验失败则直接返回 401，不再继续走后续 Controller。</p>
 *
 * <p>需要在 {@link com.xfy.nabu.web.config.WebMvcConfig} 里注册，
 * 并排除 {@code /api/v1/auth/login}、{@code /api/v1/auth/register} 等公开接口。</p>
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthInterceptor.class);

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @DubboReference
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String token = extractToken(request);
        if (token == null) {
            writeUnauthorized(response);
            return false;
        }

        Long userId;
        try {
            userId = authService.verifyToken(token);
        } catch (Exception ex) {
            log.warn("调用 AuthService.verifyToken 失败: {}", ex.getMessage());
            writeUnauthorized(response);
            return false;
        }

        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }

        // 校验通过：把 userId 写入 TraceContext，供 Controller 通过 TraceContext.getUserId() 读取，
        // 同时经由 Dubbo attachment（TTL 自动透传）传递给下游服务。
        TraceContext.setUserId(userId);
        request.setAttribute("userId", userId);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完毕后必须清理 TraceContext，避免线程池复用导致的用户上下文串号。
        TraceContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Result<Void> result = Result.failure(ResultCode.UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(result));
    }
}
