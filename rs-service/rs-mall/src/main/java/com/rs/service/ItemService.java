package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.mall.Item;

import java.util.List;

public interface ItemService {

    /**
     * 商品列表
     *
     * @return 商品列表
     */
    PageResult<Item> page(Integer page, Integer size, String sortBy, Boolean isAsc, String category);

    /**
     * 商品分类列表
     *
     * @return 商品分类列表
     */
    List<String> getCategories();

    /**
     * 商品搜索
     *
     * @return 商品搜索列表
     */
    PageResult<Item> search(Integer page, Integer size, String sortBy, Boolean isAsc, String keyword);
}
