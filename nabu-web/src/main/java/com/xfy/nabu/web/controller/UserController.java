package com.xfy.nabu.web.controller;

import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.common.result.Result;
import com.xfy.nabu.common.util.TraceContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息接口：查询当前登录用户 / 查询指定用户。
 */
@RestController
public class UserController {

    @DubboReference
    private UserService userService;

    /**
     * 查询当前登录用户信息，用户 id 来自 {@link TraceContext#getUserId()}
     * （由 {@link com.xfy.nabu.web.interceptor.JwtAuthInterceptor} 在鉴权通过后写入）。
     */
    @GetMapping("/api/v1/users/me")
    public Result<UserDTO> me() {
        Long userId = TraceContext.getUserId();
        UserDTO userDTO = userService.getById(userId);
        return Result.success(userDTO);
    }

    /**
     * 查询指定用户信息。
     */
    @GetMapping("/api/v1/users/{id}")
    public Result<UserDTO> getById(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.getById(id);
        return Result.success(userDTO);
    }
}
