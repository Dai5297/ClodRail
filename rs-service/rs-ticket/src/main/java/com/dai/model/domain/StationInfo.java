package com.dai.model.domain;

import lombok.Data;

@Data
public class StationInfo {

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
}
