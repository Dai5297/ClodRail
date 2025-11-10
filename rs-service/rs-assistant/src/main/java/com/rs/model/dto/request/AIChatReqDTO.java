package com.rs.model.dto.request;

import lombok.Data;

@Data
public class AIChatReqDTO {

    private String message;

    private String memoryId;
}
