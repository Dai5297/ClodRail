package com.rs.model.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分明细响应DTO
 */
@Data
public class PointHistoryResDTO {

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 类型: earn-获得, spend-消费
     */
    private String type;

    /**
     * 积分数量
     */
    private Integer points;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
