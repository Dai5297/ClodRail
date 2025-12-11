package com.rs.model.dto.response;

import com.rs.dto.response.ticket.SeatInfoResDTO;
import com.rs.model.domain.Passenger;
import com.rs.model.domain.Permissions;
import com.rs.model.domain.PriceDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResDTO {
    private String orderId;

    private Integer status;

    private Double totalAmount;

    private Long ticketId;

    private Long voucherId;

    private List<Passenger> passengers;

    private PriceDetail priceDetail;

    private Permissions permissions;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private LocalDateTime expireTime;

    private String trainNumber;

    private String startStation;

    private String endStation;

    private String startTime;

    private String endTime;

    private List<SeatInfoResDTO> seat;

    private Double price;
}
