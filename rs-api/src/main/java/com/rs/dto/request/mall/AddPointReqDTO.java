package com.rs.dto.request.mall;

import lombok.Data;

@Data
public class AddPointReqDTO {

    private Long userId;

    private Double price;

    private String comment;
}
