package com.xfy.nabu.forum.mapper;

import com.xfy.nabu.forum.domain.BoardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版块 Mapper。
 */
@Mapper
public interface BoardMapper {

    BoardEntity selectById(@Param("id") Long id);

    List<BoardEntity> listAll();

    int insert(BoardEntity entity);
}
