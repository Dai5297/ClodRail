package com.rs.model.domain;

import com.rs.model.dto.request.OrderCreateReqDTO;
import lombok.Data;

import java.util.List;

/**
 * Mq发送创建订单消息
 */
@Data
public class CreateOrderMessage {

    /**
     * 创建订单参数
     */
    private OrderCreateReqDTO reqDTO;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 座位ID
     */
    private List<Long> seatId;
}
