package com.rs.model.assistant;

import lombok.Data;

import java.time.LocalDateTime;

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

    /**
     * 类型
     */
    private Integer type;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 客服ID
     */
    private Long agentId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

