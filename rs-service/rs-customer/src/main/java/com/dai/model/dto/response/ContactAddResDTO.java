package com.dai.model.dto.response;

import lombok.Data;

@Data
public class ContactAddResDTO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 联系人手机号
     */
    private String phone;

    /**
     * 联系人邮箱（可选）
     */
    private String email;

    /**
     * 联系人类型: 1-成人，2-儿童，3-学生，4-老人
     */
    private Integer passengerType;

    /**
     * 是否默认联系人: 0-否，1-是
     */
    private Integer isDefault;

    /**
     * 联系人备注（可选）
     */
    private String remark;
}
