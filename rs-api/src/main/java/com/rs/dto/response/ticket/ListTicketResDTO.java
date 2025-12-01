package com.rs.dto.response.ticket;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListTicketResDTO {

    private Long id;

    private String startStation;

    private String endStation;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
