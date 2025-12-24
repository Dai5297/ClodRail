package com.rs.model.dto.response;

import com.rs.model.domain.SeatInfo;
import com.rs.model.domain.StationInfo;
import com.rs.model.domain.StopStationInfo;
import com.rs.model.domain.TrainInfo;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDetailResDTO {

    private Long id;

    private TrainInfo trainInfo;

    private StationInfo originStation;

    private StationInfo destinationStation;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Duration duration;

    private List<SeatInfo> seatTypes;

    private List<StopStationInfo> stopoverStations;
}
