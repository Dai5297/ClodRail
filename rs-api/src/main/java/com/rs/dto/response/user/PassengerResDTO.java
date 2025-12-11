package com.rs.dto.response.user;

import lombok.Data;

@Data
public class PassengerResDTO {

    private Long passengerId;

    private String name;

    private Integer passengerType;

    private String idCard;
}
