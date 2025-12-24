package com.rs.task;

import com.rs.mapper.SeatMapper;
import com.rs.mapper.TicketMapper;
import com.rs.model.domain.SeatInfo;
import com.rs.model.ticket.Ticket;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotTicketJobHandler {

    private static final DateTimeFormatter DAY_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd");

    private static final String TICKET_HOT_PREFIX = "ticket:hot:";
    private static final String TICKET_STORE_PREFIX = "ticket:store:";

    private final TicketMapper ticketMapper;
    private final SeatMapper seatMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @XxlJob("ticketRefreshTodayHotTickets")
    public ReturnT<String> refreshTodayHotTickets() {
        LocalDate today = LocalDate.now();
        String hotKey = TICKET_HOT_PREFIX + today.format(DAY_KEY_FORMATTER);

        List<Long> hotTicketIds = ticketMapper.queryHotTicketIdsByDate(today);

        stringRedisTemplate.delete(hotKey);
        if (hotTicketIds == null || hotTicketIds.isEmpty()) {
            log.info("No hot tickets for today. key={}", hotKey);
            return ReturnT.SUCCESS;
        }

        String[] members = hotTicketIds.stream().map(String::valueOf).toArray(String[]::new);
        Long added = stringRedisTemplate.opsForSet().add(hotKey, members);
        stringRedisTemplate.expire(hotKey, 2, TimeUnit.DAYS);
        log.info("Refreshed hot tickets. key={}, size={}, added={}", hotKey, hotTicketIds.size(), added);
        return ReturnT.SUCCESS;
    }

    @XxlJob("ticketPreloadHotTicketStock")
    public ReturnT<String> preloadHotTicketStock() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(15);
        LocalDateTime end = start.plusMinutes(1);

        List<Ticket> tickets = ticketMapper.queryHotTicketsStartingBetween(start, end);
        if (tickets == null || tickets.isEmpty()) {
            log.info("No hot tickets starting in [start={}, end={})", start, end);
            return ReturnT.SUCCESS;
        }

        int writeCount = 0;
        for (Ticket ticket : tickets) {
            long ttlSeconds = Duration.between(now, ticket.getStartTime().plusHours(1)).getSeconds();
            if (ttlSeconds <= 0) {
                ttlSeconds = TimeUnit.HOURS.toSeconds(1);
            }
            List<SeatInfo> seatTypes = seatMapper.querySeatTypes(ticket.getId());
            if (seatTypes == null || seatTypes.isEmpty()) {
                continue;
            }
            for (SeatInfo seatInfo : seatTypes) {
                Integer seatType = seatInfo.getType();
                Integer stock = seatMapper.queryStock(ticket.getId(), seatType);
                String stockKey = TICKET_STORE_PREFIX + ticket.getId() + ":" + seatType;
                stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(stock), ttlSeconds, TimeUnit.SECONDS);
                writeCount++;
            }
        }

        log.info("Preloaded stock for hot tickets. tickets={}, writes={}", tickets.size(), writeCount);
        return ReturnT.SUCCESS;
    }
}
