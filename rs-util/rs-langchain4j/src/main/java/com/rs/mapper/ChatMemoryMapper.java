package com.rs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMemoryMapper {
    String getMemory(@Param("memoryId") String memoryId);

    void updateMemory(@Param("memoryId") String sessionId, @Param("content") String content);

    void saveMemory(@Param("memoryId") String sessionId, @Param("content") String content);

    void deleteMemory(@Param("memoryId") String string);
}
