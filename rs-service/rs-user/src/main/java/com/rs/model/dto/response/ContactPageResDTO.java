package com.rs.model.dto.response;

import lombok.Data;

@Data
public class ContactPageResDTO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人类型: 1-成人，2-儿童，3-学生，4-老人
     */
    private Integer passengerType;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 联系人手机号
     */
    private String phone;

    /**
     * 联系人状态: 0-禁用，1-正常
     */
    private Integer status;

    /**
     * 是否默认联系人: 0-否，1-是
     */
    private Integer isDefault;
}
