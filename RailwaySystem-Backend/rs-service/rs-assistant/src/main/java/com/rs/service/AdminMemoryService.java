package com.rs.service;

import com.rs.model.dto.response.AdminMemoryResDTO;

import java.util.List;

public interface AdminMemoryService {

    /**
     * 获取所有会话列表
     * 
     * @return 会话列表
     */
    List<AdminMemoryResDTO> memoryList();

    /**
     * 获取会话详情
     * 
     * @param sessionId 会话ID
     * @return 会话详情
     */
    AdminMemoryResDTO getMemoryDetail(String sessionId);
}
