package com.rs.mapper;

import com.rs.model.assistant.Memory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话管理 Mapper
 */
@Mapper
public interface SessionMapper {

    /**
     * 获取用户的所有会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Memory> listSessionsByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话详情
     */
    Memory getSessionById(@Param("sessionId") String sessionId);

    /**
     * 创建新会话
     *
     * @param memory 会话信息
     * @return 影响行数
     */
    int createSession(Memory memory);

    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID（用于权限验证）
     * @return 影响行数
     */
    int deleteSession(@Param("sessionId") String sessionId, @Param("userId") Long userId);

    /**
     * 更新会话内容
     *
     * @param sessionId 会话ID
     * @param content   会话内容
     * @return 影响行数
     */
    int updateSessionContent(@Param("sessionId") String sessionId, @Param("content") String content);
}

