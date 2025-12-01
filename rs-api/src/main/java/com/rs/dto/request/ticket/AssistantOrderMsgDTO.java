package com.rs.dto.request.ticket;

import lombok.Data;

@Data
public class AssistantOrderMsgDTO {

    private String orderId;

    private String startStation;

    private String endStation;

    private String startTime;

    private String endTime;

    private String trainNumber;

    private Integer status;

    private Double price;
}
