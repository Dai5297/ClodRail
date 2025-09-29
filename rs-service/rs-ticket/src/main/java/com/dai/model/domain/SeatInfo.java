package com.dai.model.domain;

import lombok.Data;

@Data
public class SeatInfo {

    /**
     * 座位类型
     */
    private Integer type;

    /**
     * 座位名称
     */
    private String name;

    /**
     * 座位价格
     */
    private Double price;

    /**
     * 剩余座位数
     */
    private Integer remainingSeats;
}
