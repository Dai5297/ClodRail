package com.rs.model.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 商品兑换请求DTO
 */
@Data
public class ExchangeReqDTO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    /**
     * 兑换数量
     */
    @NotNull(message = "兑换数量不能为空")
    @Min(value = 1, message = "兑换数量至少为1")
    private Integer quantity;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String recipientName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String recipientPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String recipientAddress;
}
