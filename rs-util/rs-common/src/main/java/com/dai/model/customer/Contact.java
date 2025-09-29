package com.dai.model.customer;

import com.dai.model.BaseModel;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = true)
public class Contact extends BaseModel {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 属于的用户ID
     */
    private Long userId;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱地址（可选）
     */
    private String email;

    /**
     * 乘客类型：1-成人，2-儿童，3-学生，4-老人
     */
    private Integer passengerType;

    /**
     * 是否为默认联系人：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    private Integer isDeleted;
}
