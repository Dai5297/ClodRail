package com.rs.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderListResDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 下单车次
     */
    private Long ticketId;

    /**
     * 起始站
     */
    private String startStation;

    /**
     * 起始时间
     */
    private LocalDateTime startTime;

    /**
     * 目的站
     */
    private String endStation;

    /**
     * 截止时间
     */
    private LocalDateTime endTime;

    /**
     * 订单状态
     */
    private Integer status;
}
