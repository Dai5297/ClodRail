package com.dai.model.ticket;

import com.dai.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Train extends BaseModel {

    /**
     * 列车id
     */
    private Long id;

    /**
     * 列车名称
     */
    private String name;

    /**
     * 列车编号
     */
    private String code;

    /**
     * 列车类型
     */
    private String type;

    /**
     * 列车状态
     */
    private Integer status;

    /**
     * 一等座车厢数
     */
    private Integer firstClassCarriagesNum;

    /**
         * 二等座车厢数
     */
    private Integer secondClassCarriagesNum;

    /**
     * 一等座车厢列表数
     * 一等车厢座位数 = 一等车厢列表数 * 一等车厢数 * 4（一等车厢每列4人）
     */
    private Integer firstClassCarriagesListNum;

    /**
     * 二等座车厢列表数
     * 二等车厢座位数 = 二等车厢列表数 * 二等车厢数 * 5（二等车厢每列5人）
     */
    private Integer secondClassCarriagesListNum;
}
