package com.rs.listener;

import com.rs.client.mall.PointClient;
import com.rs.dto.request.mall.AddPointReqDTO;
import com.rs.mapper.OrderMapper;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.model.order.Order;
import com.rs.service.OrderService;
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
public class TicketOrderListener {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    private final PointClient pointClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ticket.order.queue", durable = "true"),
            exchange = @Exchange(value = "rs.ticket.order", type = ExchangeTypes.TOPIC),
            key = {"ticket.order"}
    ))
    public void ticketOrderListener(CreateTicketOrderMessage orderMessage) {
        log.info("处理订单: {}", orderMessage);
        orderService.createOrderOnSuccess(orderMessage);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ticket.order.point", durable = "true"),
            exchange = @Exchange(value = "rs.ticket.order", type = ExchangeTypes.TOPIC),
            key = {"order.point"}
    ))
    public void ticketOrderPointListener(String orderId) {
        log.info("订单赠送积分: {}", orderId);
        Order order = orderMapper.queryByOrderId(orderId);
        AddPointReqDTO addPointReqDTO = new AddPointReqDTO();
        addPointReqDTO.setUserId(order.getUserId());
        addPointReqDTO.setPrice(order.getAmount());
        addPointReqDTO.setComment("订单赠送积分");
        pointClient.addPoint(addPointReqDTO);
    }
}
