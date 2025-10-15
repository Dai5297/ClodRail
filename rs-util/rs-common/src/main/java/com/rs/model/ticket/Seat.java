package com.rs.model.ticket;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class    Seat extends BaseModel {

    /**
     * 座位id
     */
    private Long id;

    /**
     * 对应票Id
     */
    private Long ticket_id;

    /**
     * 座位类型 (1- 一等座, 2- 二等座)
     */
    private Integer seatType;

    /**
     * 车厢号
     */
    private Integer carriageNo;

    /**
     * 座位行号
     */
    private Integer seatRow;

    /**
     * 座位号
     */
    private Character searNo;

    /**
     * 完整座位编码（如01-01-A）
     */
    private String fullSeatCode;

    /**
     * 座位状态 (0- 可售, 1- 已售)
     */
    private Integer status;

    /**
     * 座位价格
     */
    private Double price;
}
