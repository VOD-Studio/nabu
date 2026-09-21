package com.xfy.nabu.api.user.service;

import com.xfy.nabu.api.user.dto.PointsChangeDTO;
import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.dto.UserRegisterDTO;
import java.util.List;

/**
 * 用户服务 Dubbo 接口。由 nabu-user-service 提供实现（@DubboService），
 * nabu-auth-service / nabu-forum-service / nabu-social-service / nabu-web 等作为消费者（@DubboReference）。
 */
public interface UserService {

    UserDTO getById(Long userId);

    List<UserDTO> listByIds(List<Long> userIds);

    UserDTO getByUsername(String username);

    /** 返回密码哈希，仅供 auth-service 内部校验登录使用，不对外暴露 */
    String getPasswordHashByUsername(String username);

    Long register(UserRegisterDTO registerDTO);

    /**
     * 积分变更。跨服务分布式事务场景（例如"100 积分购买帖子置顶"）使用 Seata AT 模式，
     * 调用方（如 forum-service）需要在自己的方法上标注 {@code @GlobalTransactional}。
     */
    void changePoints(PointsChangeDTO changeDTO);
}
