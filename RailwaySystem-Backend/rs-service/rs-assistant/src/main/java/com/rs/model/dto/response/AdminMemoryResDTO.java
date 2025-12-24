package com.rs.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMemoryResDTO {

    private String id;

    private Long userId;

    private String sessionId;

    private String userName;

    private String lastContent;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    private String content;
}
