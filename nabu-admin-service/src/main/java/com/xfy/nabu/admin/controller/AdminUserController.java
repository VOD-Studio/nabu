package com.xfy.nabu.admin.controller;

import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.common.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理端 - 用户管理示例接口。
 *
 * <p>本 Controller 只是骨架示例，展示后台管理服务如何通过 {@code @DubboReference}
 * 调用 {@link UserService} 汇总用户信息，未来可以在此基础上扩展禁用/封号/改积分等管理操作。</p>
 */
@RestController
public class AdminUserController {

    @DubboReference
    private UserService userService;

    /**
     * 查询用户详情，供管理后台展示用户信息使用。
     */
    @GetMapping("/admin/users/{id}")
    public Result<UserDTO> getUser(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.getById(id);
        return Result.success(userDTO);
    }
}
