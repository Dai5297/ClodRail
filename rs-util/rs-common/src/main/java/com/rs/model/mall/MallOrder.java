package com.rs.model.mall;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分商城订单表 (mall_order) 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MallOrder extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号 (唯一标识)
     */
    private String orderNumber;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品名称（冗余字段，方便查询）
     */
    private String itemName;

    /**
     * 商品图片（冗余字段）
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
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 备注
     */
    private String remark;
}
