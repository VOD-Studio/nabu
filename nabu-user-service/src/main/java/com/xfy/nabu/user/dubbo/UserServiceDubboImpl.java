package com.xfy.nabu.user.dubbo;

import com.xfy.nabu.api.user.dto.PointsChangeDTO;
import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.dto.UserRegisterDTO;
import com.xfy.nabu.api.user.service.UserService;
import com.xfy.nabu.user.service.UserBizService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * Dubbo Provider：对外暴露 {@link UserService}，内部转发给 {@link UserBizService}。
 * 这样拆分是为了让业务逻辑不直接依赖 Dubbo 注解，方便未来暴露 REST 管理接口时复用同一套业务代码。
 */
@DubboService
public class UserServiceDubboImpl implements UserService {

    private final UserBizService userBizService;

    public UserServiceDubboImpl(UserBizService userBizService) {
        this.userBizService = userBizService;
    }

    @Override
    public UserDTO getById(Long userId) {
        return userBizService.getById(userId);
    }

    @Override
    public List<UserDTO> listByIds(List<Long> userIds) {
        return userBizService.listByIds(userIds);
    }

    @Override
    public UserDTO getByUsername(String username) {
        return userBizService.getByUsername(username);
    }

    @Override
    public String getPasswordHashByUsername(String username) {
        return userBizService.getPasswordHashByUsername(username);
    }

    @Override
    public Long register(UserRegisterDTO registerDTO) {
        return userBizService.register(registerDTO);
    }

    @Override
    public void changePoints(PointsChangeDTO changeDTO) {
        userBizService.changePoints(changeDTO);
    }
}
