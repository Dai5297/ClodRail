package com.rs.model.ticket;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Line extends BaseModel {

    /**
     * Id
     */
    private Long id;

    /**
     * 线路名
     */
    private String name;

    /**
     * 线路码
     */
    private String code;

    /**
     * 线路总长度(Km)
     */
    private Long totalLength;

    /**
     * 线路设计速度(Km/h)
     */
    private Long designedSpeed;

    /**
     * 线路状态
     */
    private Integer status;

    /**
     * 线路起始站
     */
    private Long startStation;

    /**
     * 线路终点站
     */
    private Long endStation;
}
