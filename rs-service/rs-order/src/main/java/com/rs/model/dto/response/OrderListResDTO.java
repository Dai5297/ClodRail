package com.rs.model.dto.response;

import com.rs.dto.response.ticket.SeatInfoResDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
     * 金额
     */
    private Double amount;

    /**
     * 座位
     */
    private List<SeatInfoResDTO> seat;

    /**
     * 订单状态
     */
    private Integer status;
}
