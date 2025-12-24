package com.rs.model.dto.response;

import com.rs.dto.response.ticket.SeatInfoResDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListResDTO {

    private Long id;

    private String orderId;

    private Long ticketId;

    private Long userId;

    private String username;

    private String trainNumber;

    private String startStation;

    private LocalDateTime startTime;

    private String endStation;

    private LocalDateTime endTime;

    private Double amount;

    private LocalDateTime createTime;

    private LocalDateTime expireTime;

    private LocalDateTime payTime;

    private List<SeatInfoResDTO> seat;

    private Integer status;
}
