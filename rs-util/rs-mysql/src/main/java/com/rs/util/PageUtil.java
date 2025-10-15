package com.rs.util;

import com.rs.model.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageUtil {

    /**
     * 开始分页（设置分页参数）
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页数据量
     */
    public static void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 构建自定义分页结果
     *
     * @param list 数据列表（必须是在 PageHelper.startPage() 之后查询的结果）
     * @param <T>  数据类型
     * @return 自定义分页结果对象
     */
    public static <T> PageResult<T> buildPageResult(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(pageInfo.getTotal());          // 总记录数
        pageResult.setPages((long) pageInfo.getPages());   // 总页数
        pageResult.setCurrent((long) pageInfo.getPageNum()); // 当前页码
        pageResult.setSize((long) pageInfo.getPageSize());   // 每页大小
        pageResult.setRecords(pageInfo.getList());           // 当前页数据
        return pageResult;
    }
}
