package com.rs.task;

import com.rs.client.ticket.TicketClient;
import com.rs.client.ticket.SeatClient;
import com.rs.mapper.OrderMapper;
import com.rs.model.order.Order;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpireJobHandler {

    private static final int EXPIRED_STATUS = 6;

    private static final DefaultRedisScript<Long> SCRIPT = new DefaultRedisScript<>();

    private static final String TICKET_USER_TIME_PREFIX = "ticket:user:time:";

    private static final DateTimeFormatter DAY_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd");

    private final OrderMapper orderMapper;
    private final SeatClient seatClient;
    private final TicketClient ticketClient;
    private final StringRedisTemplate stringRedisTemplate;

    static {
        SCRIPT.setResultType(Long.class);
    }

    @XxlJob("orderExpireUnpaidOrders")
    public ReturnT<String> expireUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();

        int limit = 500;
        List<String> orderIds = orderMapper.findExpiredUnpaidOrderIds(now, limit);
        if (orderIds == null || orderIds.isEmpty()) {
            log.info("No expired unpaid orders. now={}", now);
            return ReturnT.SUCCESS;
        }

        int updated = orderMapper.markOrdersExpired(orderIds, EXPIRED_STATUS);

        List<String> expiredOrderIds = orderMapper.findOrderIdsByIdsAndStatus(orderIds, EXPIRED_STATUS);
        if (expiredOrderIds == null || expiredOrderIds.isEmpty()) {
            log.info("Expired unpaid orders updated={}, but no orders confirmed expired. scanned={}.", updated, orderIds.size());
            return ReturnT.SUCCESS;
        }

        int seatRollbackSuccess = 0;
        int seatRollbackFail = 0;
        int timeRollbackSuccess = 0;
        int timeRollbackFail = 0;

        for (String orderId : expiredOrderIds) {
            try {
                seatClient.rollbackOccupySeat(Long.valueOf(orderId));
                seatRollbackSuccess++;
            } catch (Exception e) {
                seatRollbackFail++;
                log.error("Failed to rollback seat for orderId={}", orderId);
            }

            try {
                rollbackTimeConflict(orderId);
                timeRollbackSuccess++;
            } catch (Exception e) {
                timeRollbackFail++;
                log.error("Failed to rollback time conflict for orderId={}", orderId);
            }
        }

        log.info("Expired unpaid orders. scanned={}, updated={}, confirmedExpired={}, seatRollbackSuccess={}, seatRollbackFail={}, timeRollbackSuccess={}, timeRollbackFail={}",
                orderIds.size(), updated, expiredOrderIds.size(), seatRollbackSuccess, seatRollbackFail, timeRollbackSuccess, timeRollbackFail);
        return ReturnT.SUCCESS;
    }

    private void rollbackTimeConflict(String orderId) {
        Order order = orderMapper.queryByOrderId(orderId);
        if (order == null || order.getTicketId() == null) {
            return;
        }

        var ticketMsg = ticketClient.queryOrderMsgDetail(order.getTicketId());
        if (ticketMsg == null || ticketMsg.getStartTime() == null || ticketMsg.getEndTime() == null) {
            return;
        }

        LocalDateTime startTime = ticketMsg.getStartTime();
        LocalDateTime endTime = ticketMsg.getEndTime();

        int startSite = getSite(startTime);
        int endSite = getSite(endTime);

        List<Long> passengerIds = orderMapper.queryPassengers(orderId);
        if (passengerIds == null || passengerIds.isEmpty()) {
            return;
        }

        for (Long passengerId : passengerIds) {
            if (passengerId == null) {
                continue;
            }

            if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
                String dateKey = startTime.toLocalDate().format(DAY_KEY_FORMATTER);
                SCRIPT.setLocation(new ClassPathResource("lua/RollbackRepeatTimeBitmap.lua"));
                stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(TICKET_USER_TIME_PREFIX + dateKey + ":" + passengerId),
                        String.valueOf(startSite),
                        String.valueOf(endSite)
                );
            } else {
                SCRIPT.setLocation(new ClassPathResource("lua/RollbackRepeatTimeCrossDay.lua"));
                String startDateKey = TICKET_USER_TIME_PREFIX + startTime.toLocalDate().format(DAY_KEY_FORMATTER) + ":" + passengerId;
                String endDateKey = TICKET_USER_TIME_PREFIX + endTime.toLocalDate().format(DAY_KEY_FORMATTER) + ":" + passengerId;
                stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(startDateKey, endDateKey),
                        String.valueOf(startSite),
                        String.valueOf(endSite),
                        String.valueOf(startTime.toLocalDate().toEpochDay()),
                        String.valueOf(endTime.toLocalDate().toEpochDay())
                );
            }
        }
    }

    private int getSite(LocalDateTime time) {
        LocalTime localTime = time.toLocalTime();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        return (hour * 60 + minute) / 20;
    }
}
