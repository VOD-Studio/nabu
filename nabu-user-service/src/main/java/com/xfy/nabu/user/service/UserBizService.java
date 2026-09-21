package com.xfy.nabu.user.service;

import com.xfy.nabu.api.user.dto.PointsChangeDTO;
import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.dto.UserRegisterDTO;
import java.util.List;

/**
 * 内部业务服务接口（区别于对外的 Dubbo {@code UserService}），供本模块内部（Dubbo 实现类 / 未来的 REST 管理接口）复用。
 */
public interface UserBizService {

    UserDTO getById(Long userId);

    List<UserDTO> listByIds(List<Long> userIds);

    UserDTO getByUsername(String username);

    String getPasswordHashByUsername(String username);

    Long register(UserRegisterDTO registerDTO);

    void changePoints(PointsChangeDTO changeDTO);
}
