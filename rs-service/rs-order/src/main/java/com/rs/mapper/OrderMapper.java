package com.rs.mapper;

import com.rs.model.order.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 是否创建成功
     */
    boolean createOrder(Order order);

    /**
     * 创建订单座位关系
     *
     * @param orderId 订单ID
     * @param seatId  座位ID
     */
    void createOrderSeat(Long orderId, Long seatId);
}
