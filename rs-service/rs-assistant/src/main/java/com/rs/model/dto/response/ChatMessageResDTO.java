package com.rs.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息响应DTO
 */
@Data
@Schema(description = "聊天消息")
public class ChatMessageResDTO {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", example = "1")
    private Long id;

    /**
     * 消息类型（user/ai/human/system）
     */
    @Schema(description = "消息类型", example = "user")
    private String type;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", example = "我想查询明天北京到上海的车票")
    private String content;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳", example = "2024-11-09T22:30:00")
    private LocalDateTime timestamp;
}

