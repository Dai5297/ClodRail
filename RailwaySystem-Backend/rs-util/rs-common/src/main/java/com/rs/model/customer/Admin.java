package com.rs.model.customer;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseModel {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（已加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 头像
     */
    private String icon;

    /**
     * 角色ID（注意：单角色设计，若需多角色应移除此字段，改用关联表）
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 账号状态：0-禁用，1-启用
     */
    private Integer status;
}
