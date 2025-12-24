package com.rs.model.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商城订单响应DTO
 */
@Data
public class MallOrderResDTO {

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品图片
     */
    private String itemImage;

    /**
     * 兑换数量
     */
    private Integer quantity;

    /**
     * 单价（积分）
     */
    private Integer unitPrice;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 收货人姓名
     */
    private String recipientName;

    /**
     * 联系电话
     */
    private String recipientPhone;

    /**
     * 收货地址
     */
    private String recipientAddress;

    /**
     * 订单状态：0-待发货，1-已发货，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 订单状态文本
     */
    private String statusText;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime completeTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
