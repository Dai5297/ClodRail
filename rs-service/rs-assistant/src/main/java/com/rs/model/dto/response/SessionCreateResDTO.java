package com.rs.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建会话响应DTO
 */
@Data
@Schema(description = "创建会话响应")
public class SessionCreateResDTO {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "123_1699999999999")
    private String sessionId;

    /**
     * 会话标题
     */
    @Schema(description = "会话标题", example = "新对话")
    private String title;

    /**
     * 会话类型
     */
    @Schema(description = "会话类型", example = "ai")
    private String type;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-11-09T22:30:00")
    private LocalDateTime createTime;
}

