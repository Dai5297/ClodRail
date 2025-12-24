package com.rs.model.domain;

import lombok.Data;

@Data
public class Passenger {

    private Long passengerId;

    private String name;

    private Integer passengerType;

    private String idCard;

    private String seatPosition;

    private Double actualPrice;
}
