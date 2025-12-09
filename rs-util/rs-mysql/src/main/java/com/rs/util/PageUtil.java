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

    /**
     * 构建自定义分页结果（从原始分页结果中获取真实总数）
     *
     * @param originalPageList 原始分页结果
     * @param convertedList    转换后的数据列表
     * @param <T>              原始数据类型
     * @param <R>              转换后的数据类型
     * @return 自定义分页结果对象
     */
    public static <T, R> PageResult<R> buildPageResultFromSource(List<T> originalPageList, List<R> convertedList) {
        // originalPageList 必须是 PageHelper 返回的 Page<T>
        if (!(originalPageList instanceof com.github.pagehelper.Page)) {
            throw new IllegalArgumentException("originalPageList must be a PageHelper Page instance");
        }

        com.github.pagehelper.Page<T> page = (com.github.pagehelper.Page<T>) originalPageList;
        PageResult<R> result = new PageResult<>();
        result.setTotal(page.getTotal());          // 真实总数
        result.setPages((long) page.getPages());   // 总页数
        result.setCurrent((long) page.getPageNum());// 当前页
        result.setSize((long) page.getPageSize());  // 每页大小
        result.setRecords(convertedList);           // 转换后的数据
        return result;
    }
}
