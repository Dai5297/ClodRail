package com.rs.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话信息响应DTO
 */
@Data
@Schema(description = "会话信息")
public class ChatSessionResDTO {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "123_1699999999999")
    private String sessionId;

    /**
     * 会话标题
     */
    @Schema(description = "会话标题", example = "关于购票的咨询")
    private String title;

    /**
     * 会话类型（ai/human）
     */
    @Schema(description = "会话类型", example = "ai")
    private String type;

    /**
     * 最后一条消息
     */
    @Schema(description = "最后一条消息", example = "您好，请问有什么可以帮您？")
    private String lastMessage;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-11-09T22:30:00")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-11-09T22:00:00")
    private LocalDateTime createTime;
}

