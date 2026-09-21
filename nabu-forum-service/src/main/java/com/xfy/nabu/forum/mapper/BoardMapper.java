package com.xfy.nabu.forum.mapper;

import com.xfy.nabu.forum.domain.BoardEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版块 Mapper。
 */
@Mapper
public interface BoardMapper {

    BoardEntity selectById(@Param("id") Long id);

    List<BoardEntity> listAll();

    int insert(BoardEntity entity);
}
