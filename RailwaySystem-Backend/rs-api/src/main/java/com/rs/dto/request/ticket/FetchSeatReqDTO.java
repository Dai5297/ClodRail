package com.rs.dto.request.ticket;

import lombok.Data;

@Data
public class FetchSeatReqDTO {

    private Long ticketId;

    private String orderId;

    private Integer seatType;

    private String seatPosition;
}
