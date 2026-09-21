package com.xfy.nabu.file.mapper;

import com.xfy.nabu.file.domain.FileMetaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMetaMapper {

    int insert(FileMetaEntity entity);

    FileMetaEntity selectById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
