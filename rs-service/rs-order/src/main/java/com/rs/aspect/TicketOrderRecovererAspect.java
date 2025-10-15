package com.rs.aspect;

import cn.hutool.json.JSONUtil;
import com.rs.mapper.FailedTicketOrderMsgMapper;
import com.rs.model.domain.CreateOrderMessage;
import com.rs.model.domain.Passenger;
import com.rs.model.order.TicketOrderFailedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TicketOrderRecovererAspect {

    private final FailedTicketOrderMsgMapper failedTicketOrderMsgMapper;

    @After("execution(* com.rs.plugins.ErrorMessageRecoverer.recover(org.springframework.amqp.core.Message, Throwable))")
    public void afterRecover(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Message message = (Message) args[0];
        Throwable throwable = (Throwable) args[1];
        log.info("异常消费消息{}存入数据库", message.getBody());
        TicketOrderFailedMessage ticketOrderFailedMessage = new TicketOrderFailedMessage();
        ticketOrderFailedMessage.setMessageId(message.getMessageProperties().getMessageId());
        CreateOrderMessage createOrderMessage = JSONUtil.toBean(new String(message.getBody()), CreateOrderMessage.class);
        ticketOrderFailedMessage.setOrderId(createOrderMessage.getOrderId());
        ticketOrderFailedMessage.setPassengerId(
                createOrderMessage.getReqDTO()
                        .getPassengers()
                        .stream()
                        .map(Passenger::getPassengerId)
                        .toList()
        );
        ticketOrderFailedMessage.setSeatId(createOrderMessage.getSeatId());
        ticketOrderFailedMessage.setBody(JSONUtil.toJsonStr(createOrderMessage));
        ticketOrderFailedMessage.setRetryCount(getRetryCount(message));
        ticketOrderFailedMessage.setFailReason(throwable.getMessage());
        failedTicketOrderMsgMapper.saveFailedMsg(ticketOrderFailedMessage);
    }

    /**
     * 获取消息的失败次数
     *
     * @param message 消息
     * @return 失败次数
     */
    private int getRetryCount(Message message) {
        Object death = message.getMessageProperties().getHeader("x-death");
        if (death instanceof List && !((List<?>) death).isEmpty()) {
            Map<String, Object> deathEntry = (Map<String, Object>) ((List<?>) death).get(0);
            Object count = deathEntry.get("count");
            if (count instanceof Integer) {
                return (Integer) count;
            }
        }
        return 0;
    }
}
