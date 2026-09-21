package com.xfy.nabu.notify.mapper;

import com.xfy.nabu.notify.domain.NotificationEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {

    int insert(NotificationEntity entity);

    List<NotificationEntity> selectByReceiver(
            @Param("receiverId") Long receiverId, @Param("offset") long offset, @Param("limit") long limit);

    long countByReceiver(@Param("receiverId") Long receiverId);

    long countUnread(@Param("receiverId") Long receiverId);

    int markRead(@Param("receiverId") Long receiverId, @Param("id") Long id);

    int markAllRead(@Param("receiverId") Long receiverId);
}
