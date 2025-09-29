package com.dai.model.dto.response;

import lombok.Data;

@Data
public class StationResDTO {

    /**
     * 站点id
     */
    private Long id;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 站点编码
     */
    private String code;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 是否是热门站点
     */
    private Integer isHot;
}
