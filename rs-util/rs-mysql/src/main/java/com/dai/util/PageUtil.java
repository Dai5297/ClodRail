package com.dai.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageUtil {
    /**
     * 开始分页
     * @param pageNum 页码
     * @param pageSize 每页数据量
     */
    public static void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }
    /**
     * 构建分页结果
     * @param list 数据列表
     * @param <T> 数据类型
     * @return 分页信息对象
     */
    public static <T> PageInfo<T> buildPageInfo(List<T> list) {
        return new PageInfo<>(list);
    }
}
