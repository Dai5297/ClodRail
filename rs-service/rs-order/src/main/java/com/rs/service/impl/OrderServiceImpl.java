package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.rs.annotation.Lock;
import com.rs.client.RabbitClient;
import com.rs.client.customer.ContactClient;
import com.rs.client.ticket.SeatClient;
import com.rs.client.ticket.TicketClient;
import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.customer.PassengerResDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.OrderMapper;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.model.domain.Passenger;
import com.rs.model.domain.PriceDetail;
import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.order.Order;
import com.rs.model.ticket.Seat;
import com.rs.service.OrderService;
import com.rs.util.RedisIdUtil;
import com.rs.util.UserContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.rs.constant.RedisKeyConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAspectJAutoProxy(exposeProxy = true)
public class OrderServiceImpl implements OrderService {

    private final SeatClient seatClient;

    private final TicketClient ticketClient;

    private final ContactClient contactClient;

    private final RedisIdUtil redisIdUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final RabbitClient rabbitClient;

    private final OrderMapper orderMapper;

    private static final DefaultRedisScript<Long> SCRIPT = new DefaultRedisScript<>();

    static {
        SCRIPT.setResultType(Long.class);
    }

    /**
     * 创建订单
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    @TwoPhaseBusinessAction(name = "createOrder", commitMethod = "commit", rollbackMethod = "rollback")
    @Override
    public OrderCreateResDTO createOrder(BusinessActionContext context, OrderCreateReqDTO reqDTO) {
        // 预检查时间冲突车票
        // TODO 方便测试取消时间冲突验证
        // preCheckRepeatTime(reqDTO);
        String hotKey = TICKET_HOT + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey, String.valueOf(reqDTO.getTicketId()));
        if (Boolean.TRUE.equals(isHotTicket)) {
            // 热门车票处理 需要进行预占座位和预检测余票
            return seckillTicket(context, reqDTO);
        } else {
            // 非热门车票处理 直接下单
            OrderService proxy = (OrderService) AopContext.currentProxy();
            return proxy.commonCreateOrder(reqDTO);
        }
    }

    @Override
    public boolean commit(BusinessActionContext context) {
        log.info("TCC Commit阶段开始执行");
        JSONObject reqDTOJson = (JSONObject) context.getActionContext("reqDTO");
        OrderCreateReqDTO reqDTO = null;
        if (reqDTOJson != null) {
            reqDTO = reqDTOJson.toJavaObject(OrderCreateReqDTO.class);
        }
        if (reqDTO != null) {
            String hotKey = TICKET_HOT + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey, String.valueOf(reqDTO.getTicketId()));
            if (isHotTicket == null || !isHotTicket) {
                return true;
            }
            String orderId = stringRedisTemplate.opsForValue().get(TICKET_ORDER_ID + context.getBranchId());
            if (orderId != null) {
                CreateTicketOrderMessage orderMessage = new CreateTicketOrderMessage(
                        orderId,
                        reqDTO.getPassengers()
                                .stream()
                                .map(Passenger::getPassengerId)
                                .toList()
                );
                rabbitClient.sendMsg("rs.ticket.order", "ticket.order", orderMessage);
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

        Map<String, Object> contextData = context.getActionContext();
        if (contextData != null) {
            JSONObject object = (JSONObject) contextData.get("reqDTO");
            OrderCreateReqDTO reqDTO = object.toJavaObject(OrderCreateReqDTO.class);
            String hotKey = TICKET_HOT + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            String orderId = stringRedisTemplate.opsForValue().get(TICKET_ORDER_ID + context.getBranchId());
            Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey, String.valueOf(reqDTO.getTicketId()));
            // 回滚时间设置
            rollbackRepeatTime(reqDTO);
            if (isHotTicket == null || !isHotTicket) {
                return true;
            }

            // 回滚已插入到seat表的座位订单
            if (orderId != null) {
                seatClient.rollbackOccupySeat(Long.valueOf(orderId));
                stringRedisTemplate.delete(TICKET_ORDER_ID + context.getBranchId());
            }
            // 回滚余票设置 - 只有热门票且已扣减余票时才需要回滚
            String tag = stringRedisTemplate.opsForValue().get(TICKET_DEDUCTION_TAG + context.getBranchId());
            if (tag != null && tag.equals("1")) {
                rollbackRemainingTicket(reqDTO);
            }

            // 减去销量
            String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            stringRedisTemplate.opsForValue().decrement(ICR_TICKET_ORDER + data);

            // 回滚座位占用 - 只有热门票且有orderId时才需要回滚
            if (orderId != null) {
                seatClient.rollbackOccupySeat(Long.valueOf(orderId));
            }
        }

        log.info("TCC Rollback阶段执行完成");
        return true;
    }

    @Override
    @Transactional
    public void createOrderOnSuccess(CreateTicketOrderMessage orderMessage) {
        String orderId = orderMessage.getOrderId();
        String orderJsonStr = stringRedisTemplate.opsForValue().get(TICKET_ORDER_INFO + orderId);
        Order order = JSONUtil.toBean(orderJsonStr, Order.class);
        order.setCreateBy(UserContext.get());
        order.setUpdateBy(UserContext.get());
        orderMapper.createOrder(order);
        for (Long passengerId : orderMessage.getPassengerIds()) {
            Object object = stringRedisTemplate.opsForHash().get(TICKET_SEAT + orderId, String.valueOf(passengerId));
            Long seatId = Long.valueOf(object.toString());
            orderMapper.createOrderSeat(orderId, seatId, passengerId);
        }
    }

    /**
     * 预检测余票
     *
     * @param reqDTO 创建参数
     */
    private void preCheckRemainingTicket(BusinessActionContext context, OrderCreateReqDTO reqDTO) {
        int size = reqDTO.getPassengers().size();

        SCRIPT.setLocation(new ClassPathResource("lua/CheckRemainingTicket.lua"));
        Long result = stringRedisTemplate.execute(
                SCRIPT,
                List.of(TICKET_STORE + reqDTO.getTicketId() + ":" + reqDTO.getSeatType()),
                String.valueOf(size)
        );
        if (result == 0) {
            throw new CommonException(RespCode.TICKET_SEAT_NOT_ENOUGH, "余票不足");
        }
        stringRedisTemplate.opsForValue().set(
                TICKET_DEDUCTION_TAG + context.getBranchId(),
                String.valueOf(1),
                TICKET_DEDUCTION_TAG_TTL,
                TimeUnit.SECONDS
        );
    }

    /**
     * 回滚余票
     *
     * @param reqDTO 创建参数
     */
    private void rollbackRemainingTicket(OrderCreateReqDTO reqDTO) {
        int size = reqDTO.getPassengers().size();
        SCRIPT.setLocation(new ClassPathResource("lua/RollbackRemainingTicket.lua"));
        stringRedisTemplate.execute(
                SCRIPT,
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
            // 计算开始和结束时间的bitmap位置
            int startSite = getSite(reqDTO.getStartTime());
            int endSite = getSite(reqDTO.getEndTime());

            // 获取开始和结束日期
            LocalDate startDate = reqDTO.getStartTime().toLocalDate();
            LocalDate endDate = reqDTO.getEndTime().toLocalDate();

            if (startDate.equals(endDate)) {
                // 同一天的情况
                String dateKey = startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
                SCRIPT.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTimeBitmap.lua"));
                Long result = stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(TICKET_USER_TIME + dateKey + ":" + passenger.getPassengerId()),
                        String.valueOf(startSite),
                        String.valueOf(endSite)
                );
                if (result == 0) {
                    throw new CommonException(RespCode.TICKET_ORDER_CREATE_FAIL, "购票时间存在冲突");
                }
            } else {
                // 跨天的情况 - 使用改进的跨天处理逻辑
                SCRIPT.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTimeCrossDay.lua"));

                // 构建参数：开始日期key、结束日期key、开始位置、结束位置、乘客ID
                String startDateKey = TICKET_USER_TIME + startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":" + passenger.getPassengerId();
                String endDateKey = TICKET_USER_TIME + endDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":" + passenger.getPassengerId();

                Long result = stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(startDateKey, endDateKey),
                        String.valueOf(startSite),
                        String.valueOf(endSite),
                        String.valueOf(startDate.toEpochDay()),
                        String.valueOf(endDate.toEpochDay())
                );
                if (result == 0) {
                    throw new CommonException(RespCode.TICKET_ORDER_CREATE_FAIL, "购票时间存在冲突");
                }
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
            // 计算开始和结束时间的bitmap位置
            int startSite = getSite(reqDTO.getStartTime());
            int endSite = getSite(reqDTO.getEndTime());

            // 获取开始和结束日期
            LocalDate startDate = reqDTO.getStartTime().toLocalDate();
            LocalDate endDate = reqDTO.getEndTime().toLocalDate();


            if (startDate.equals(endDate)) {
                // 同一天的情况
                String dateKey = startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
                SCRIPT.setLocation(new ClassPathResource("lua/RollbackRepeatTimeBitmap.lua"));

                stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(TICKET_USER_TIME + dateKey + ":" + passenger.getPassengerId()),
                        String.valueOf(startSite),
                        String.valueOf(endSite)
                );
            } else {
                // 跨天的情况 - 使用改进的跨天回滚逻辑
                SCRIPT.setLocation(new ClassPathResource("lua/RollbackRepeatTimeCrossDay.lua"));

                // 构建参数：开始日期key、结束日期key、开始位置、结束位置
                String startDateKey = TICKET_USER_TIME + startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":" + passenger.getPassengerId();
                String endDateKey = TICKET_USER_TIME + endDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":" + passenger.getPassengerId();

                stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(startDateKey, endDateKey),
                        String.valueOf(startSite),
                        String.valueOf(endSite),
                        String.valueOf(startDate.toEpochDay()),
                        String.valueOf(endDate.toEpochDay())
                );
            }
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
    @Override
    @Lock(formatter = "order:#{reqDTO.getTicketId()}")
    public OrderCreateResDTO commonCreateOrder(OrderCreateReqDTO reqDTO) {
        // 检测库存
        String orderId = redisIdUtil.nextId(ICR_TICKET_ORDER);
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(UserContext.get());
        order.setTicketId(reqDTO.getTicketId());
        order.setAmount(reqDTO.getAmount());
        order.setExpireTime(LocalDateTime.now().plusMinutes(15L));
        Map<Long, Long> seatMaps = new HashMap<>();
        for (Passenger passenger : reqDTO.getPassengers()) {
            FetchSeatReqDTO fetchSeatReqDTO = new FetchSeatReqDTO();
            fetchSeatReqDTO.setOrderId(orderId);
            fetchSeatReqDTO.setTicketId(reqDTO.getTicketId());
            fetchSeatReqDTO.setSeatType(reqDTO.getSeatType());
            fetchSeatReqDTO.setSeatPosition(passenger.getSeatPosition());
            FetchSeatResDTO seat = seatClient.fetchSeat(fetchSeatReqDTO);
            if (seat == null) {
                throw new CommonException(RespCode.TICKET_ORDER_CREATE_FAIL, "无座位可分配");
            }
            passenger.setSeatPosition(seat.getSeatNo());
            seatMaps.put(passenger.getPassengerId(), seat.getId());
        }
        orderMapper.createOrder(order);
        seatMaps.forEach((passengerId, seatId) -> {
            orderMapper.createOrderSeat(orderId, seatId, passengerId);
        });
        OrderCreateResDTO resDTO = new OrderCreateResDTO();
        BeanUtil.copyProperties(reqDTO, resDTO);
        resDTO.setId(orderId);
        resDTO.setStatus(0);
        resDTO.setCreateTime(LocalDateTime.now());
        resDTO.setExpireTime(LocalDateTime.now().plusMinutes(15));
        return resDTO;
    }

    @Override
    public OrderDetailResDTO orderDetail(String orderId) {
        OrderDetailResDTO detailResDTO = orderMapper.queryDetail(orderId);
        List<Long> passengerIds = orderMapper.queryPassengers(orderId);
        PriceDetail priceDetail = new PriceDetail();
        List<Passenger> passengers = new ArrayList<>();
        List<PriceDetail.Breakdown> breakdowns = new ArrayList<>();
        Double totalBaseAmount = 0D;
        Double totalDiscountAmount = 0D;
        for (PassengerResDTO passengerResDTO : contactClient.queryPassenger(passengerIds)) {
            Passenger passenger = BeanUtil.copyProperties(passengerResDTO, Passenger.class);
            Seat seat = seatClient.querySeat(orderId);
            passenger.setSeatPosition(seat.getFullSeatCode());
            passengers.add(passenger);
            PriceDetail.Breakdown breakdown = new PriceDetail.Breakdown();
            breakdown.setPassengerName(passenger.getName());
            breakdown.setPassengerType(passenger.getPassengerType());
            Double basePrice = ticketClient.queryTicketPrice(detailResDTO.getTicketId(), seat.getSeatType());
            breakdown.setBasePrice(basePrice);
            totalBaseAmount += breakdown.getBasePrice();
            if (passenger.getPassengerType() != 1) {
                breakdown.setDiscount(breakdown.getBasePrice() * 0.2);
                breakdown.setActualPrice(breakdown.getBasePrice() - breakdown.getDiscount());
                totalDiscountAmount += breakdown.getDiscount();
            }else {
                breakdown.setActualPrice(breakdown.getBasePrice());
            }
            breakdowns.add(breakdown);
        }
        priceDetail.setBreakdown(breakdowns);
        priceDetail.setBaseAmount(totalBaseAmount);
        priceDetail.setDiscountAmount(totalDiscountAmount);
        priceDetail.setTotalAmount(totalBaseAmount - totalDiscountAmount);
        detailResDTO.setPriceDetail(priceDetail);
        detailResDTO.setPassengers(passengers);
        detailResDTO.setPermissions(orderMapper.queryPermission(orderId));
        return detailResDTO;
    }

    /**
     * 秒杀车票处理
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    private OrderCreateResDTO seckillTicket(BusinessActionContext context, OrderCreateReqDTO reqDTO) {
        // 预检测余票
        preCheckRemainingTicket(context, reqDTO);
        OrderCreateResDTO resDTO = new OrderCreateResDTO();
        // 开始生成订单Id
        String orderId = redisIdUtil.nextId(ICR_TICKET_ORDER);
        stringRedisTemplate.opsForValue().set(
                TICKET_ORDER_ID + context.getBranchId(),
                String.valueOf(orderId),
                TICKET_ORDER_ID_TTL,
                TimeUnit.SECONDS);
        // 使用Redis暂存订单信息
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(UserContext.get());
        order.setTicketId(reqDTO.getTicketId());
        order.setAmount(reqDTO.getAmount());
        for (Passenger passenger : reqDTO.getPassengers()) {
            // 为每个乘客获取到座位
            FetchSeatReqDTO fetchSeatReqDTO = new FetchSeatReqDTO();
            fetchSeatReqDTO.setSeatPosition(passenger.getSeatPosition());
            fetchSeatReqDTO.setSeatType(reqDTO.getSeatType());
            fetchSeatReqDTO.setTicketId(reqDTO.getTicketId());
            fetchSeatReqDTO.setOrderId(orderId);
            FetchSeatResDTO seat = seatClient.fetchSeat(fetchSeatReqDTO);
            if (seat == null) {
                throw new CommonException(RespCode.TICKET_SEAT_NOT_ENOUGH, "余票不足");
            }
            passenger.setSeatPosition(seat.getFullSeatCode());
            // 使用redis的散列存储乘客-座位信息
            stringRedisTemplate.opsForHash().put(TICKET_SEAT + orderId,
                    String.valueOf(passenger.getPassengerId()),
                    String.valueOf(seat.getId()));
            stringRedisTemplate.expire(TICKET_SEAT + orderId, TICKET_SEAT_TTL, TimeUnit.SECONDS);
        }
        BeanUtil.copyProperties(reqDTO, resDTO);
        resDTO.setId(orderId);
        resDTO.setStatus(0);
        resDTO.setCreateTime(LocalDateTime.now());
        resDTO.setExpireTime(LocalDateTime.now().plusMinutes(15));
        stringRedisTemplate.opsForValue().set(
                TICKET_ORDER_INFO + orderId,
                JSONUtil.toJsonStr(order),
                TICKET_ORDER_INFO_TTL,
                TimeUnit.SECONDS);
        // 使用Branchid存储订单Id
        return resDTO;
    }
}
