package com.rs;

import com.rs.core.ElasticSearchTemplate;
import com.rs.mapper.ItemMapper;
import com.rs.model.mall.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EsTest {

    @Autowired
    private ElasticSearchTemplate elasticSearchTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void test() {
        List<Item> items = itemMapper.selectAll();
        int batchSize = 500;
        System.out.println("Total items: " + items.size());

        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            List<Item> batch = items.subList(i, end);
            elasticSearchTemplate.opsForDoc().batchInsert("item", batch);
        }
    }
}
