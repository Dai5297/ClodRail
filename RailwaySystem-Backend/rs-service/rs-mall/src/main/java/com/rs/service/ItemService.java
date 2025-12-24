package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.mall.Item;

import java.util.List;

public interface ItemService {

    PageResult<Item> page(Integer page, Integer size, String sortBy, Boolean isAsc, String category);

    List<String> getCategories();

    PageResult<Item> search(Integer page, Integer size, String sortBy, Boolean isAsc, String keyword);

    PageResult<Item> adminPage(String name, String category, Integer status, Integer pageNum, Integer pageSize);

    void add(Item item);

    void update(Item item);

    void updateStatus(Long id, Integer status);

    void delete(Long id);
}
