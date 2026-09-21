package com.xfy.nabu.notify.service.impl;

import com.xfy.nabu.api.notify.dto.NotificationDTO;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.notify.domain.NotificationEntity;
import com.xfy.nabu.notify.mapper.NotificationMapper;
import com.xfy.nabu.notify.service.NotifyBizService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotifyBizServiceImpl implements NotifyBizService {

    private final NotificationMapper notificationMapper;

    public NotifyBizServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public PageResult<NotificationDTO> listByReceiver(Long receiverId, long pageNum, long pageSize) {
        long offset = (pageNum - 1) * pageSize;
        List<NotificationEntity> entities = notificationMapper.selectByReceiver(receiverId, offset, pageSize);
        long total = notificationMapper.countByReceiver(receiverId);
        List<NotificationDTO> dtoList = entities == null ? Collections.emptyList()
                : entities.stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResult<>(dtoList, total, pageNum, pageSize);
    }

    @Override
    public long countUnread(Long receiverId) {
        return notificationMapper.countUnread(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long receiverId, Long notificationId) {
        notificationMapper.markRead(receiverId, notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long receiverId) {
        notificationMapper.markAllRead(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotification(Long receiverId, String type, String content, String linkUrl) {
        NotificationEntity entity = new NotificationEntity();
        entity.setReceiverId(receiverId);
        entity.setType(type);
        entity.setContent(content);
        entity.setLinkUrl(linkUrl);
        entity.setIsRead(0);
        notificationMapper.insert(entity);
    }

    private NotificationDTO toDTO(NotificationEntity entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setReceiverId(entity.getReceiverId());
        dto.setType(entity.getType());
        dto.setContent(entity.getContent());
        dto.setLinkUrl(entity.getLinkUrl());
        dto.setRead(entity.getIsRead() != null && entity.getIsRead() == 1);
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
