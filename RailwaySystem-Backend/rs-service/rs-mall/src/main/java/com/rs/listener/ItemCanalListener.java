package com.rs.listener;

import cn.hutool.json.JSONUtil;
import com.rs.core.ElasticSearchTemplate;
import com.rs.model.entity.CanalMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemCanalListener {

    private final ElasticSearchTemplate elasticSearchTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "canal.item.queue", durable = "true"),
                    exchange = @Exchange(value = "canal-exchange", type = ExchangeTypes.TOPIC),
                    key = "canal.rs-mall.item"
            ),
            containerFactory = "canalListenerContainerFactory"
    )
    public void canalOrderListener(String message) {
        // 处理 item 表变更
        log.info("处理 item 表变更: {}", message);
        CanalMsg canalMsg = JSONUtil.toBean(message, CanalMsg.class);
        switch (canalMsg.getType()) {
            case "INSERT":
                elasticSearchTemplate.opsForDoc().batchInsert("item", canalMsg.getData());
                break;
            case "UPDATE":
                elasticSearchTemplate.opsForDoc().batchUpsert("item", canalMsg.getData());
                break;
            case "DELETE":
                elasticSearchTemplate.opsForDoc().batchDelete("item", canalMsg.getOld());
                break;
            default:
                break;
        }
    }

}
