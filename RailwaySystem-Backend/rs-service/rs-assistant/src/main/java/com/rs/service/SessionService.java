package com.rs.service;

import com.rs.model.dto.response.ChatMessageResDTO;
import com.rs.model.dto.response.ChatSessionResDTO;
import com.rs.model.dto.response.SessionCreateResDTO;

import java.util.List;

/**
 * 会话管理服务接口
 */
public interface SessionService {

    /**
     * 获取会话列表
     *
     * @return 会话列表
     */
    List<ChatSessionResDTO> getSessions();

    /**
     * 获取会话消息历史
     *
     * @param sessionId 会话ID
     * @return 消息历史列表
     */
    List<ChatMessageResDTO> getChatHistory(String sessionId);

    /**
     * 创建新会话
     *
     * @return 新会话信息
     */
    SessionCreateResDTO createSession();

    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);
}

