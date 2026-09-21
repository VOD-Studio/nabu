package com.xfy.nabu.user.service.impl;

import com.xfy.nabu.api.user.dto.PointsChangeDTO;
import com.xfy.nabu.api.user.dto.UserDTO;
import com.xfy.nabu.api.user.dto.UserRegisterDTO;
import com.xfy.nabu.common.exception.BusinessException;
import com.xfy.nabu.common.result.ResultCode;
import com.xfy.nabu.user.domain.UserEntity;
import com.xfy.nabu.user.mapper.UserMapper;
import com.xfy.nabu.user.service.UserBizService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBizServiceImpl implements UserBizService {

    private final UserMapper userMapper;

    public UserBizServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getById(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        return entity == null ? null : toDTO(entity);
    }

    @Override
    public List<UserDTO> listByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(userIds).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getByUsername(String username) {
        UserEntity entity = userMapper.selectByUsername(username);
        return entity == null ? null : toDTO(entity);
    }

    @Override
    public String getPasswordHashByUsername(String username) {
        UserEntity entity = userMapper.selectByUsername(username);
        return entity == null ? null : entity.getPasswordHash();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO registerDTO) {
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(ResultCode.PARAM_INVALID.getCode(), "用户名已存在");
        }
        UserEntity entity = new UserEntity();
        entity.setUsername(registerDTO.getUsername());
        entity.setPasswordHash(registerDTO.getPasswordHash());
        entity.setNickname(registerDTO.getNickname() == null ? registerDTO.getUsername() : registerDTO.getNickname());
        userMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePoints(PointsChangeDTO changeDTO) {
        // TODO: idempotentKey 建议落一张 points_change_log 表做唯一约束，防止 MQ/RPC 重试重复加减分。
        int affected = userMapper.changePoints(changeDTO.getUserId(), changeDTO.getDelta());
        if (affected == 0) {
            throw new BusinessException(ResultCode.PARAM_INVALID.getCode(), "积分不足或用户不存在");
        }
    }

    private UserDTO toDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setNickname(entity.getNickname());
        dto.setAvatar(entity.getAvatar());
        dto.setLevel(entity.getLevel());
        dto.setPoints(entity.getPoints());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
