package com.dai.model;

import lombok.Data;

import java.util.List;

@Data
public class  PageResult<T> {

    /**
     * 总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 数据
     */
    private List<T> records;
}
