package com.rs.model.domain;

import lombok.Data;

@Data
public class Permissions {

    /**
     * 是否可以支付
     */
    private Boolean canPay;

    /**
     * 是否可以取消
     */
    private Boolean canCancel;

    /**
     * 是否可以改签
     */
    private Boolean canChange;
}
