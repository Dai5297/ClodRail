package com.rs.model.dto.request;

import com.rs.model.domain.Passenger;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateReqDTO {

    private Long ticketId;

    private Integer seatType;

    private Double amount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<Passenger> passengers;
}
