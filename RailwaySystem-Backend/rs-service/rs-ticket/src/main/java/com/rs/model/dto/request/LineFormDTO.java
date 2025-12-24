package com.rs.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class LineFormDTO {

    private Long id;

    private String name;

    private String code;

    private Long totalLength;

    private Long designedSpeed;

    private Integer status;

    private Long startStation;

    private Long endStation;

    private List<Long> stopStations;
}
