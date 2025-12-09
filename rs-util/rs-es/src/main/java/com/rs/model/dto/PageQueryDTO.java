package com.rs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class PageQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String orderBy1;
    /**
     * 排序字段1是否升序
     */
    private Boolean isAsc1 = false;

    /**
     * 排序字段2
     */
    private String orderBy2;
    /**
     * 排序字段2是否升序
     */
    private Boolean isAsc2 = false;

    /**
     * 计算起始条数（从0开始）
     * @return 起始偏移量
     */
    public long calFrom() {
        // pageNum 通常从 1 开始，所以要减 1
        if (pageNum == null || pageNum <= 0) {
            return 0;
        }
        if (pageSize == null || pageSize <= 0) {
            return 0;
        }
        return (pageNum.longValue() - 1) * pageSize.longValue();
    }
    @AllArgsConstructor
    @Getter
    public static class OrderBy {
        private String orderBy;
        private Boolean isAsc;
    }
}
