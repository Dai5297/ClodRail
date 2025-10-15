package com.rs.model.ticket;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Ticket extends BaseModel {

    /**
     * 车票ID
     */
    private Long id;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 火车ID
     */
    private Long trainId;

    /**
     * 出发时间
     */
    private LocalDateTime startTime;

    /**
     * 到达时间
     */
    private LocalDateTime endTime;

    /**
     * 状态(0-未开售 1-正在售票 2-售空)
     */
    private String status;

    /**
     * 是否热门车次(0-否 1-是)
     */
    private Integer isHot;

    /**
     * 一等座价格
     */
    private Double firstClassPrice;

    /**
     * 二等座价格
     */
    private Double secondClassPrice;
}
