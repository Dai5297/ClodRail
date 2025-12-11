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

    /**
     * 扣减库存
     *
     * @param itemId   商品ID
     * @param quantity 数量
     * @return 影响行数
     */
    int deductStock(Long itemId, Integer quantity);

    /**
     * 增加销量
     *
     * @param itemId   商品ID
     * @param quantity 数量
     * @return 影响行数
     */
    int increaseSold(Long itemId, Integer quantity);
}
