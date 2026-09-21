package com.xfy.nabu.user.mapper;

import com.xfy.nabu.user.domain.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    UserEntity selectById(@Param("id") Long id);

    List<UserEntity> selectByIds(@Param("ids") List<Long> ids);

    UserEntity selectByUsername(@Param("username") String username);

    int insert(UserEntity entity);

    /**
     * 原子扣/加积分：{@code points = points + delta}，避免读改写并发问题；
     * 通过 WHERE points + delta >= 0 防止超扣。返回受影响行数，0 表示余额不足。
     */
    int changePoints(@Param("id") Long id, @Param("delta") Integer delta);
}
