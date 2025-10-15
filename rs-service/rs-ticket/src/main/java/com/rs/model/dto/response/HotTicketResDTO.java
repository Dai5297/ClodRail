package com.rs.model.dto.response;

import com.rs.model.domain.StationInfo;
import lombok.Data;

import java.time.Duration;

@Data
public class HotTicketResDTO {

    /**
     * 车票id
     */
    private Long id;

    /**
     * 出发站
     */
    private StationInfo originStation;

    /**
     * 到达站
     */
    private StationInfo destinationStation;

    /**
     * 线路名称
     */
    private String routeName;

    /**
     * 最低价格
     */
    private Double minPrice;

    /**
     * 平均时长
     */
    private Duration avgDuration;

    /**
     * 人气
     */
    private Double popularity;
}
