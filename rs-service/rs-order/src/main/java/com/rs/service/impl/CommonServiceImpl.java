package com.rs.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.rs.client.ticket.TicketClient;
import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.mapper.OrderMapper;
import com.rs.model.order.Order;
import com.rs.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.rs.constant.RedisAssistantKeyyConstant.ASSISTANT_ORDER;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final OrderMapper orderMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final TicketClient ticketClient;

    @Override
    public String generateAssistantOrder(String id) {
        Order order = orderMapper.queryByOrderId(id);
        String uuid = UUID.randomUUID().toString();
        String key = ASSISTANT_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + uuid;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(order));
        return uuid;
    }

    @Override
    public AssistantOrderMsgDTO praseAssistantOrder(String uuid) {
        String key = ASSISTANT_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + uuid;
        String orderStr = stringRedisTemplate.opsForValue().get(key);
        Order order = JSONUtil.toBean(orderStr, Order.class);
        AssistantOrderMsgDTO assistantOrderMsgDTO = ticketClient.queryOrderMsgDetail(order.getTicketId());
        assistantOrderMsgDTO.setOrderId(order.getOrderId());
        assistantOrderMsgDTO.setPrice(order.getAmount());
        assistantOrderMsgDTO.setStatus(order.getStatus());
        return assistantOrderMsgDTO;
    }
}
