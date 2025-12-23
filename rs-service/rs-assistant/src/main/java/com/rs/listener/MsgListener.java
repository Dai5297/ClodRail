package com.rs.listener;

import cn.hutool.json.JSONUtil;
import com.rs.mapper.MemoryMapper;
import com.rs.model.assistant.Memory;
import com.rs.model.entity.Message;
import com.rs.model.entity.MsgContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.rs.constant.RedisAssistantKeyyConstant.ASSISTANT_ORDER;

@Slf4j
@Component
@RequiredArgsConstructor
public class MsgListener {

    private final MemoryMapper memoryMapper;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用户消息监听
     *
     * @param message 消息
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "assistant.user.msg.queue", durable = "true"),
                    exchange = @Exchange(value = "rs.assistant.msg", type = ExchangeTypes.TOPIC),
                    key = {"assistant.user"}
            )
    )
    public void userMsgListener(String message) {
        log.info("用户消息：{}", message);
        Message msg = JSONUtil.toBean(message, Message.class);
        Memory memory = memoryMapper.getMemory(msg.getSessionId());
        if (memory == null) {
            memory = new Memory();
            memory.setSessionId(msg.getSessionId());
            memory.setUserId(Long.valueOf(msg.getFrom()));
            memory.setAgentId(Long.valueOf(msg.getTo()));
            MsgContent msgContent = new MsgContent();
            msgContent.setContent(msg.getContent());
            msgContent.setType(msg.getType());
            List<MsgContent> contents = new ArrayList<>();
            contents.add(msgContent);
            memory.setContent(JSONUtil.toJsonStr(contents));
            memoryMapper.addMemory(memory);
            return;
        }
        List<MsgContent> list = JSONUtil.toList(memory.getContent(), MsgContent.class);
        list.add(new MsgContent(msg.getType(), msg.getContent()));
        memory.setContent(JSONUtil.toJsonStr(list));
        if (memory.getUserId() == null) {
            memory.setUserId(Long.valueOf(msg.getFrom()));
        }
        if (memory.getAgentId() == null) {
            memory.setAgentId(Long.valueOf(msg.getTo()));
        }
        memoryMapper.updateMemory(memory);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "assistant.order.msg.queue", durable = "true"),
                    exchange = @Exchange(value = "rs.assistant.msg", type = ExchangeTypes.TOPIC),
                    key = {"assistant.order"}
            )
    )
    public void orderMsgListener(String message) {
        log.info("订单消息：{}", message);
        Message msg = JSONUtil.toBean(message, Message.class);
        Memory memory = memoryMapper.getMemory(msg.getSessionId());
        if (memory == null) {
            memory = new Memory();
            memory.setSessionId(msg.getSessionId());
            memory.setUserId(Long.valueOf(msg.getTo()));
            memory.setAgentId(Long.valueOf(msg.getFrom()));
            MsgContent msgContent = new MsgContent();
            String key = ASSISTANT_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + msg.getContent();
            String orderStr = stringRedisTemplate.opsForValue().get(key);
            msgContent.setContent(orderStr);
            msgContent.setType(msg.getType());
            List<MsgContent> contents = new ArrayList<>();
            contents.add(msgContent);
            memory.setContent(JSONUtil.toJsonStr(contents));
            memoryMapper.addMemory(memory);
        } else {
            List<MsgContent> list = JSONUtil.toList(memory.getContent(), MsgContent.class);
            list.add(new MsgContent(msg.getType(), msg.getContent()));
            memory.setContent(JSONUtil.toJsonStr(list));
            if (memory.getUserId() == null) {
                memory.setUserId(Long.valueOf(msg.getFrom()));
            }
            if (memory.getAgentId() == null) {
                memory.setAgentId(Long.valueOf(msg.getTo()));
            }
            memoryMapper.updateMemory(memory);        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "assistant.agent.msg.queue", durable = "true"),
                    exchange = @Exchange(value = "rs.assistant.msg", type = ExchangeTypes.TOPIC),
                    key = {"assistant.agent"}
            )
    )
    public void agentMsgListener(String message) {
        log.info("客服消息：{}", message);
        Message msg = JSONUtil.toBean(message, Message.class);
        Memory memory = memoryMapper.getMemoryByAgentAndUserId(msg.getFrom(), msg.getTo());
        if (memory == null) {
            memory = new Memory();
            memory.setSessionId(msg.getSessionId());
            memory.setUserId(Long.valueOf(msg.getTo()));
            memory.setAgentId(Long.valueOf(msg.getFrom()));
            MsgContent msgContent = new MsgContent();
            msgContent.setContent(msg.getContent());
            msgContent.setType(msg.getType());
            List<MsgContent> contents = new ArrayList<>();
            contents.add(msgContent);
            memory.setContent(JSONUtil.toJsonStr(contents));
            memoryMapper.addMemory(memory);
        } else {
            List<MsgContent> list = JSONUtil.toList(memory.getContent(), MsgContent.class);
            list.add(new MsgContent(msg.getType(), msg.getContent()));
            memory.setContent(JSONUtil.toJsonStr(list));
            if (memory.getUserId() == null) {
                memory.setUserId(Long.valueOf(msg.getFrom()));
            }
            if (memory.getAgentId() == null) {
                memory.setAgentId(Long.valueOf(msg.getTo()));
            }
            memoryMapper.updateMemory(memory);        }
    }
}
