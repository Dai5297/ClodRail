package com.dai.user.model.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dai.model.BaseModel;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String icon;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;
}
