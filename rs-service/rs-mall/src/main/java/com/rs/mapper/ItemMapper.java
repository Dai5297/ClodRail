package com.rs.mapper;

import com.rs.model.mall.Item;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {

    Item getItemById(Long id);

    List<Item> pageQuery(String sortBy, String category);

    List<String> getCategories();

    List<Item> selectAll();
}
