package com.rs.dto.request.ticket;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssistantOrderMsgDTO {

    private String orderId;

    private String startStation;

    private String endStation;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String trainNumber;

    private Integer status;

    private Double price;
}
