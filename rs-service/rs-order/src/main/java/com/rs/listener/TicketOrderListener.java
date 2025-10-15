package com.rs.listener;

import com.rs.model.domain.CreateTicketOrderMessage;
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ticket.order.queue", durable = "true"),
            exchange = @Exchange(value = "rs.ticket.order", type = ExchangeTypes.TOPIC),
            key = {"ticket.order"}
    ))
    public void ticketOrderListener(CreateTicketOrderMessage orderMessage) {
        log.info("处理订单: {}", orderMessage);
        orderService.createOrderOnSuccess(orderMessage);
    }
}
