package com.rs.model.ticket;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Station extends BaseModel {

    /**
     * Id
     */
    private Long id;

    /**
     * 站名
     */
    private String name;

    /**
     * 站码
     */
    private String code;

    /**
     * 地址
     */
    private String address;

    /**
     * 所属城市
     */
    private String city;

    /**
     * 所属省份
     */
    private String province;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否热门
     */
    private Integer isHot;

    /**
     * 描述
     */
    private String description;

    /**
     * 联系电话
     */
    private String phone;
}
