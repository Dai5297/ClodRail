package com.rs.model.entity;

import lombok.Data;

/**
 * 会话记忆实体
 */
@Data
public class Memory {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 历史对话内容
     */
    private String content;
}

