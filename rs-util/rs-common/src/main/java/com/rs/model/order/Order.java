package com.rs.model.order;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseModel {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 下单用户
     */
    private Long userId;

    /**
     * 下单车次
     */
    private Long ticketId;

    /**
     * 订单状态：0- 待支付, 1- 已支付, 2- 已出票, 3- 已取消, 4- 已退票, 5- 已改签
     */
    private Integer status;

    /**
     * 支付方式：0- 支付宝, 1- 微信
     */
    private Integer paymentMethod;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 费用（单位：分）
     */
    private Double amount;
}
