package com.rs.model.dto.response;

import com.rs.model.domain.SeatInfo;
import com.rs.model.domain.StationInfo;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchTicketResDTO {

    private Long id;

    private Long trainId;

    private String trainName;

    private String trainNumber;

    private StationInfo originStation;

    private StationInfo destinationStation;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Duration duration;

    List<SeatInfo> seatTypes;
}
