package com.rs.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.rs.client.RabbitClient;
import com.rs.model.entity.CanalFlatMessage;
import com.rs.model.entity.CanalMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanalListener {

    private final RabbitClient rabbitClient;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "canal.queue", durable = "true"),
                    exchange = @Exchange(value = "canal-exchange", type = ExchangeTypes.TOPIC),
                    key = "example"
            ),
            containerFactory = "canalListenerContainerFactory"
    )
    public void canalListener(String message) {
        CanalFlatMessage canalMsg = JSONUtil.toBean(message, CanalFlatMessage.class);
        CanalMsg sendMsg = BeanUtil.copyProperties(canalMsg, CanalMsg.class);
        String key = generateKey(canalMsg.getDatabase(), canalMsg.getTable());
        rabbitClient.sendMsg("canal-exchange", key, sendMsg);
    }

    private String generateKey(String database, String table) {
        return "canal" +
                "." +
                database +
                "." +
                table;
    }

}
