package com.rs.service.impl;

import com.rs.core.ElasticSearchTemplate;
import com.rs.mapper.ItemMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.PageQueryDTO;
import com.rs.model.mall.Item;
import com.rs.service.ItemService;
import com.rs.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final ElasticSearchTemplate elasticSearchTemplate;

    @Override
    public PageResult<Item> page(Integer page, Integer size, String sortBy, Boolean isAsc, String category) {
        PageUtil.startPage(page, size);
        if (sortBy != null && isAsc != null) {
            if (isAsc) {
                sortBy = sortBy + " ASC";
            }else {
                sortBy = sortBy + " DESC";
            }
        }
        List<Item> items = itemMapper.pageQuery(sortBy, category);
        return PageUtil.buildPageResult(items);
    }

    @Override
    public List<String> getCategories() {
        List<String> list = stringRedisTemplate.opsForList().range("mall:items:categories", 0, -1);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        list = itemMapper.getCategories();
        stringRedisTemplate.opsForList().rightPushAll("mall:items:categories", list);
        return list;
    }

    @Override
    public PageResult<Item> search(Integer page, Integer size, String sortBy, Boolean isAsc, String keyword) {
        PageQueryDTO pageQueryDTO = new PageQueryDTO();
        pageQueryDTO.setPageNum(page);
        pageQueryDTO.setPageSize(size);
        pageQueryDTO.setOrderBy1(sortBy);
        pageQueryDTO.setIsAsc1(isAsc);
        return elasticSearchTemplate.opsForDoc().searchForPage("item", keyword, pageQueryDTO, Item.class);
    }
}
