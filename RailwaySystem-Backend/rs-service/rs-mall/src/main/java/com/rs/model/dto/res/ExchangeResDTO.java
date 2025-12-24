package com.rs.model.dto.res;

import lombok.Data;

/**
 * 商品兑换响应DTO
 */
@Data
public class ExchangeResDTO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 兑换订单号
     */
    private String orderNumber;

    /**
     * 剩余积分
     */
    private Long remainingPoints;

    public ExchangeResDTO(Boolean success, String orderNumber, Long remainingPoints) {
        this.success = success;
        this.orderNumber = orderNumber;
        this.remainingPoints = remainingPoints;
    }
}
