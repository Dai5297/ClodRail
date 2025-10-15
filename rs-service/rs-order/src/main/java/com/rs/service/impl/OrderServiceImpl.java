package com.rs.service.impl;

import client.ticket.SeatClient;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.rs.client.RabbitClient;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.OrderMapper;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.model.domain.Passenger;
import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.model.order.Order;
import com.rs.service.OrderService;
import com.rs.util.RedisIdUtil;
import com.rs.util.UserContext;
import dto.request.FetchSeatReqDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.LocalTCC;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.rs.constant.RedisKeyConstant.*;

@LocalTCC
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ApplicationContext applicationContextAware;

    private final SeatClient seatClient;

    private final RedisIdUtil redisIdUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final RabbitClient rabbitClient;

    private final OrderMapper orderMapper;


    @Override
    @GlobalTransactional
    public OrderCreateResDTO test(OrderCreateReqDTO reqDTO) {
        OrderService bean = applicationContextAware.getBean(OrderService.class);
        return bean.createOrder(reqDTO);
    }

    /**
     * 创建订单
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    @Override
    public OrderCreateResDTO createOrder(OrderCreateReqDTO reqDTO) {
        // 预检测余票
        preCheckRemainingTicket(reqDTO);
        // 预检查时间冲突车票
        preCheckRepeatTime(reqDTO);
        String hotKey = TICKET_HOT + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey, String.valueOf(reqDTO.getTicketId()));
        if (Boolean.TRUE.equals(isHotTicket)) {
            // 热门车票处理 需要进行预占座位
            return seckillTicket(reqDTO);
        } else {
            // 非热门车票处理 直接下单
            return commonCreateOrder(reqDTO);
        }
    }


    @Override
    public boolean commit(BusinessActionContext context) {
        log.info("TCC Commit阶段开始执行");
        OrderCreateReqDTO reqDTO = (OrderCreateReqDTO) context.getActionContext("reqDTO");
        if (reqDTO != null) {
            Long passengerId = reqDTO.getPassengers().get(0).getPassengerId();
            String orderId = stringRedisTemplate.opsForValue().get(TICKET_ORDER_ID + passengerId);
            if (orderId != null) {
                CreateTicketOrderMessage orderMessage = new CreateTicketOrderMessage(
                        Long.valueOf(orderId),
                        reqDTO.getPassengers()
                                .stream()
                                .map(Passenger::getPassengerId)
                                .toList()
                );
                String orderMsgStr = JSONUtil.toJsonStr(orderMessage);
                rabbitClient.sendMsg("rs.ticket.order", "ticket.order", orderMsgStr);
                log.info("TCC Commit阶段执行完成，订单ID: {}", orderId);
            } else {
                log.error("TCC Commit阶段失败，订单不存在");
                throw new CommonException(RespCode.ORDER_NOT_EXIST, "订单不存在");
            }
        }
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext context) {
        log.info("TCC Rollback阶段开始执行");
        OrderCreateReqDTO reqDTO = (OrderCreateReqDTO) context.getActionContext("reqDTO");
        if (reqDTO != null) {
            // 回滚时间设置
            rollbackRepeatTime(reqDTO);
            // 回滚余票设置
            rollbackRemainingTicket(reqDTO);
            // 减去销量
            String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            stringRedisTemplate.opsForValue().decrement("icr:" + TICKET_ORDER + data);
            log.info("TCC Rollback阶段执行完成");
        }
        return true;
    }

    @Override
    @Transactional
    public void createOrderOnSuccess(CreateTicketOrderMessage orderMessage) {
        Long orderId = orderMessage.getOrderId();
        String orderJsonStr = stringRedisTemplate.opsForValue().get(TICKET_ORDER + orderId);
        Order order = JSONUtil.toBean(orderJsonStr, Order.class);
        orderMapper.createOrder(order);
        for (Long passengerId : orderMessage.getPassengerIds()) {
            Long seatId = (Long) stringRedisTemplate.opsForHash().get(TICKET_SEAT + orderId, passengerId);
            orderMapper.createOrderSeat(orderId, seatId);
        }
    }

    /**
     * 预检测余票
     *
     * @param reqDTO 创建参数
     */
    private void preCheckRemainingTicket(OrderCreateReqDTO reqDTO) {
        int size = reqDTO.getPassengers().size();
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/CheckRemainingTicket.lua"));
        script.setResultType(Long.class);
        Long result = stringRedisTemplate.execute(
                script,
                List.of(TICKET_STORE + reqDTO.getTicketId() + ":" + reqDTO.getSeatType()),
                String.valueOf(size)
        );
        if (result == 0) {
            throw new CommonException(RespCode.TICKET_SEAT_NOT_ENOUGH, "余票不足");
        }
    }

    /**
     * 回滚余票
     *
     * @param reqDTO 创建参数
     */
    private void rollbackRemainingTicket(OrderCreateReqDTO reqDTO) {
        int size = reqDTO.getPassengers().size();
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/RollbackRemainingTicket.lua"));
        script.setResultType(Long.class);
        stringRedisTemplate.execute(
                script,
                List.of(TICKET_STORE + reqDTO.getTicketId() + ":" + reqDTO.getSeatType()),
                String.valueOf(size)
        );
    }

    /**
     * 预检查时间冲突车票
     *
     * @param reqDTO 创建参数
     */
    private void preCheckRepeatTime(OrderCreateReqDTO reqDTO) {
        List<Passenger> passengers = reqDTO.getPassengers();
        for (Passenger passenger : passengers) {
            int startSite = getSite(reqDTO.getStartTime());
            int endSite = getSite(reqDTO.getEndTime());
            String data = reqDTO.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTime.lua"));
            script.setResultType(Long.class);
            Long result = stringRedisTemplate.execute(
                    script,
                    List.of(TICKET_USER_TIME + data + ":" + passenger.getPassengerId()),
                    String.valueOf(startSite),
                    String.valueOf(endSite)
            );
            if (result == 0) {
                throw new CommonException(RespCode.TICKET_ORDER_CREATE_FAIL, "购票时间存在冲突");
            }
        }
    }

    /**
     * 回滚时间冲突车票
     *
     * @param reqDTO 创建参数
     */
    private void rollbackRepeatTime(OrderCreateReqDTO reqDTO) {
        List<Passenger> passengers = reqDTO.getPassengers();
        for (Passenger passenger : passengers) {
            int startSite = getSite(reqDTO.getStartTime());
            int endSite = getSite(reqDTO.getEndTime());
            String data = reqDTO.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setLocation(new ClassPathResource("lua/RollbackRepeatTime.lua"));
            stringRedisTemplate.execute(
                    script,
                    List.of(TICKET_USER_TIME + data + ":" + passenger.getPassengerId()),
                    String.valueOf(startSite),
                    String.valueOf(endSite)
            );
        }
    }

    /**
     * 获取时间间隔对应的bitMap位(20分钟间隔)
     *
     * @param time 时间
     * @return 位
     */
    private int getSite(LocalDateTime time) {
        LocalTime localTime = time.toLocalTime();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        // 在bitMap中每一位时间间隔为20min
        return (hour * 60 + minute) / 20;
    }

    /**
     * 普通创建订单(用于非热门车票 直接访问数据库创建订单)
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    private OrderCreateResDTO commonCreateOrder(OrderCreateReqDTO reqDTO) {
        return null;
    }

    /**
     * 秒杀车票处理
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    private OrderCreateResDTO seckillTicket(OrderCreateReqDTO reqDTO) {
        OrderCreateResDTO resDTO = new OrderCreateResDTO();
        // 开始生成订单Id
        BeanUtil.copyProperties(reqDTO, resDTO);
        Long orderId = redisIdUtil.nextId("icr:" + TICKET_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        resDTO.setId(orderId);
        resDTO.setStatus(0);
        resDTO.setCreateTime(LocalDateTime.now());
        resDTO.setExpireTime(LocalDateTime.now().plusMinutes(15));
        // 使用Redis暂存订单信息
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(UserContext.get());
        order.setTicketId(reqDTO.getTicketId());
        order.setAmount(reqDTO.getAmount());
        stringRedisTemplate.opsForValue().set(TICKET_ORDER + orderId, JSONUtil.toJsonStr(order));
        for (Passenger passenger : reqDTO.getPassengers()) {
            // 为每个乘客获取到座位
            FetchSeatReqDTO fetchSeatReqDTO = new FetchSeatReqDTO();
            fetchSeatReqDTO.setSeatPosition(passenger.getSeatPosition());
            fetchSeatReqDTO.setSeatType(reqDTO.getSeatType());
            fetchSeatReqDTO.setTicketId(reqDTO.getTicketId());
            Long seatId = seatClient.fetchSeat(fetchSeatReqDTO);
            // 使用redis的散列存储乘客-座位信息
            stringRedisTemplate.opsForHash().put(TICKET_SEAT + orderId, String.valueOf(passenger.getPassengerId()), String.valueOf(seatId));
        }
        // 使用第一个乘客的id存储订单Id
        stringRedisTemplate.opsForValue().set(TICKET_ORDER_ID + reqDTO.getPassengers().get(0).getPassengerId(), String.valueOf(orderId));
        return resDTO;
    }
}
