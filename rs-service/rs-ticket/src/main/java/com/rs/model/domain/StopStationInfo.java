package com.rs.model.domain;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class StopStationInfo {

    private Long id;

    private String name;

    private String code;

    private LocalDateTime arriveTime;

    private LocalDateTime departTime;

    private Duration stopDuration;
}
