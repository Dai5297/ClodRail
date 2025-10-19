package com.rs.dto.request;

import lombok.Data;

@Data
public class FetchSeatReqDTO {

    private Long ticketId;

    private Long orderId;

    private Integer seatType;

    private String seatPosition;
}
