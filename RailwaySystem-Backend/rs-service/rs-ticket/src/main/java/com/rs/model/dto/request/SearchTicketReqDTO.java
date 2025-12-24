package com.rs.model.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchTicketReqDTO {

    private Long originStationId;

    private Long destinationStationId;

    private LocalDate departureDate;

    private Integer pageNum;

    private Integer pageSize;
}
