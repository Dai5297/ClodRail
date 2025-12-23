package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.rs.annotation.Lock;
import com.rs.client.RabbitClient;
import com.rs.client.ticket.SeatClient;
import com.rs.client.ticket.TicketClient;
import com.rs.client.user.ContactClient;
import com.rs.client.user.UserClient;
import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.dto.response.ticket.SeatInfoResDTO;
import com.rs.dto.response.ticket.SeatTypeInfoResDTO;
import com.rs.dto.response.user.PassengerResDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.OrderMapper;
import com.rs.model.PageResult;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.model.domain.Passenger;
import com.rs.model.domain.PriceDetail;
import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.dto.response.OrderListResDTO;
import com.rs.model.dto.response.OrderStatisticsResDTO;
import com.rs.model.order.Order;
import com.rs.model.ticket.Seat;
import com.rs.service.OrderService;
import com.rs.util.PageUtil;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private final UserClient userClient;

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
        preCheckRepeatTime(reqDTO);
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
            Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey,
                    String.valueOf(reqDTO.getTicketId()));
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
                                .toList());
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
            Boolean isHotTicket = stringRedisTemplate.opsForSet().isMember(hotKey,
                    String.valueOf(reqDTO.getTicketId()));
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
                String.valueOf(size));
        if (result == 0) {
            throw new CommonException(RespCode.TICKET_SEAT_NOT_ENOUGH, "余票不足");
        }
        stringRedisTemplate.opsForValue().set(
                TICKET_DEDUCTION_TAG + context.getBranchId(),
                String.valueOf(1),
                TICKET_DEDUCTION_TAG_TTL,
                TimeUnit.SECONDS);
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
                String.valueOf(size));
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
                        String.valueOf(endSite));
                if (result == 0) {
                    throw new CommonException(RespCode.TICKET_ORDER_CREATE_FAIL, "购票时间存在冲突");
                }
            } else {
                // 跨天的情况 - 使用改进的跨天处理逻辑
                SCRIPT.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTimeCrossDay.lua"));

                // 构建参数：开始日期key、结束日期key、开始位置、结束位置、乘客ID
                String startDateKey = TICKET_USER_TIME + startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))
                        + ":" + passenger.getPassengerId();
                String endDateKey = TICKET_USER_TIME + endDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":"
                        + passenger.getPassengerId();

                Long result = stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(startDateKey, endDateKey),
                        String.valueOf(startSite),
                        String.valueOf(endSite),
                        String.valueOf(startDate.toEpochDay()),
                        String.valueOf(endDate.toEpochDay()));
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
                        String.valueOf(endSite));
            } else {
                // 跨天的情况 - 使用改进的跨天回滚逻辑
                SCRIPT.setLocation(new ClassPathResource("lua/RollbackRepeatTimeCrossDay.lua"));

                // 构建参数：开始日期key、结束日期key、开始位置、结束位置
                String startDateKey = TICKET_USER_TIME + startDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))
                        + ":" + passenger.getPassengerId();
                String endDateKey = TICKET_USER_TIME + endDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + ":"
                        + passenger.getPassengerId();

                stringRedisTemplate.execute(
                        SCRIPT,
                        List.of(startDateKey, endDateKey),
                        String.valueOf(startSite),
                        String.valueOf(endSite),
                        String.valueOf(startDate.toEpochDay()),
                        String.valueOf(endDate.toEpochDay()));
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
        rabbitClient.sendMsg("rs.ticket.order", "order.point", orderId);
        return resDTO;
    }

    @Override
    public OrderDetailResDTO orderDetail(String orderId) {
        OrderDetailResDTO detailResDTO = orderMapper.queryDetail(orderId);

        // 补充车次信息
        try {
            AssistantOrderMsgDTO ticketMsg = ticketClient.queryOrderMsgDetail(detailResDTO.getTicketId());
            if (ticketMsg != null) {
                detailResDTO.setTrainNumber(ticketMsg.getTrainNumber());
                detailResDTO.setStartStation(ticketMsg.getStartStation());
                detailResDTO.setEndStation(ticketMsg.getEndStation());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                detailResDTO.setStartTime(ticketMsg.getStartTime().format(formatter));
                detailResDTO.setEndTime(ticketMsg.getEndTime().format(formatter));
            }
        } catch (Exception e) {
            log.error("获取车票信息失败: {}", orderId, e);
        }

        List<Long> passengerIds = orderMapper.queryPassengers(orderId);
        PriceDetail priceDetail = new PriceDetail();
        List<Passenger> passengers = new ArrayList<>();
        List<PriceDetail.Breakdown> breakdowns = new ArrayList<>();
        List<SeatInfoResDTO> seatList = new ArrayList<>();
        Double totalBaseAmount = 0D;
        Double totalDiscountAmount = 0D;
        for (PassengerResDTO passengerResDTO : contactClient.queryPassenger(passengerIds)) {
            Passenger passenger = BeanUtil.copyProperties(passengerResDTO, Passenger.class);
            Long seatId = orderMapper.querySeatIdByPassenger(orderId, passengerResDTO.getPassengerId());
            Seat seat = seatId == null ? null : seatClient.querySeatById(seatId);
            if (seat != null) {
                passenger.setSeatPosition(seat.getFullSeatCode());
            }

            // 设置座位信息
            SeatInfoResDTO seatInfo = new SeatInfoResDTO();
            if (seat != null) {
                seatInfo.setSeatType(seat.getSeatType());
                seatInfo.setFullSeatCode(seat.getFullSeatCode());
            }
            seatList.add(seatInfo);

            passengers.add(passenger);
            PriceDetail.Breakdown breakdown = new PriceDetail.Breakdown();
            breakdown.setPassengerName(passenger.getName());
            breakdown.setPassengerType(passenger.getPassengerType());
            Double basePrice = seat == null ? 0D : ticketClient.queryTicketPrice(detailResDTO.getTicketId(), seat.getSeatType());
            breakdown.setBasePrice(basePrice);
            totalBaseAmount += breakdown.getBasePrice();
            if (passenger.getPassengerType() != 1) {
                breakdown.setDiscount(breakdown.getBasePrice() * 0.2);
                breakdown.setActualPrice(breakdown.getBasePrice() - breakdown.getDiscount());
                totalDiscountAmount += breakdown.getDiscount();
            } else {
                breakdown.setActualPrice(breakdown.getBasePrice());
            }
            passenger.setActualPrice(breakdown.getActualPrice());
            breakdowns.add(breakdown);
        }
        priceDetail.setBreakdown(breakdowns);
        priceDetail.setBaseAmount(totalBaseAmount);
        priceDetail.setDiscountAmount(totalDiscountAmount);
        priceDetail.setTotalAmount(totalBaseAmount - totalDiscountAmount);
        detailResDTO.setPriceDetail(priceDetail);
        detailResDTO.setPrice(priceDetail.getTotalAmount());
        detailResDTO.setSeat(seatList);
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

    @Override
    public PageResult<OrderListResDTO> list(Integer pageNum, Integer pageSize, String orderId, Integer status) {
        PageUtil.startPage(pageNum, pageSize);
        Long userId = UserContext.get();
        List<Order> orders = orderMapper.findByUserId(userId, orderId, status);
        List<OrderListResDTO> listResDTOS = new ArrayList<>();
        for (Order order : orders) {
            listResDTOS.add(BeanUtil.copyProperties(order, OrderListResDTO.class));
        }
        List<Long> ticketIds = listResDTOS.stream().map(OrderListResDTO::getTicketId).toList();
        List<ListTicketResDTO> list = ticketClient.list(ticketIds);
        for (OrderListResDTO resDTO : listResDTOS) {
            for (ListTicketResDTO listTicketResDTO : list) {
                if (resDTO.getTicketId().equals(listTicketResDTO.getId())) {
                    resDTO.setStartStation(listTicketResDTO.getStartStation());
                    resDTO.setEndStation(listTicketResDTO.getEndStation());
                    resDTO.setStartTime(listTicketResDTO.getStartTime());
                    resDTO.setEndTime(listTicketResDTO.getEndTime());
                }
            }
        }
        List<String> orderIds = listResDTOS.stream().map(OrderListResDTO::getOrderId).toList();
        List<SeatTypeInfoResDTO> seatTypeInfoResDTOS = seatClient.ListSeatQuery(orderIds);
        for (OrderListResDTO resDTO : listResDTOS) {
            for (SeatTypeInfoResDTO seatTypeInfoResDTO : seatTypeInfoResDTOS) {
                if (resDTO.getOrderId().equals(seatTypeInfoResDTO.getOrderId())) {
                    resDTO.setSeat(seatTypeInfoResDTO.getSeatInfo());
                }
            }
        }
        return PageUtil.buildPageResultFromSource(orders, listResDTOS);
    }

    @Override
    public PageResult<OrderListResDTO> adminPage(Integer pageNum, Integer pageSize, String orderId, Integer status,
                                                Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        PageUtil.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.findAdminPage(orderId, status, userId, startTime, endTime);
        if (orders == null || orders.isEmpty()) {
            return PageUtil.buildPageResult(List.of());
        }

        List<OrderListResDTO> listResDTOS = new ArrayList<>();
        for (Order order : orders) {
            listResDTOS.add(BeanUtil.copyProperties(order, OrderListResDTO.class));
        }

        List<Long> ticketIds = listResDTOS.stream()
                .map(OrderListResDTO::getTicketId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (!ticketIds.isEmpty()) {
            List<ListTicketResDTO> ticketList = ticketClient.list(ticketIds);
            Map<Long, ListTicketResDTO> ticketMap = ticketList.stream()
                    .collect(Collectors.toMap(ListTicketResDTO::getId, t -> t));
            for (OrderListResDTO dto : listResDTOS) {
                ListTicketResDTO ticket = ticketMap.get(dto.getTicketId());
                if (ticket != null) {
                    dto.setTrainNumber(ticket.getTrainNumber());
                    dto.setStartStation(ticket.getStartStation());
                    dto.setEndStation(ticket.getEndStation());
                    dto.setStartTime(ticket.getStartTime());
                    dto.setEndTime(ticket.getEndTime());
                }
            }
        }

        List<Long> userIds = listResDTOS.stream()
                .map(OrderListResDTO::getUserId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (!userIds.isEmpty()) {
            try {
                Map<Long, String> usernameMap = userClient.usernameList(userIds);
                for (OrderListResDTO dto : listResDTOS) {
                    if (dto.getUserId() != null && usernameMap.containsKey(dto.getUserId())) {
                        dto.setUsername(usernameMap.get(dto.getUserId()));
                    }
                }
            } catch (Exception e) {
                log.error("获取用户名列表失败", e);
            }
        }

        List<String> orderIds = listResDTOS.stream()
                .map(OrderListResDTO::getOrderId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (!orderIds.isEmpty()) {
            try {
                List<SeatTypeInfoResDTO> seatTypeInfoResDTOS = seatClient.ListSeatQuery(orderIds);
                for (OrderListResDTO dto : listResDTOS) {
                    for (SeatTypeInfoResDTO seatTypeInfoResDTO : seatTypeInfoResDTOS) {
                        if (dto.getOrderId().equals(seatTypeInfoResDTO.getOrderId())) {
                            dto.setSeat(seatTypeInfoResDTO.getSeatInfo());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取订单座位信息失败", e);
            }
        }

        return PageUtil.buildPageResultFromSource(orders, listResDTOS);
    }

    @Override
    public OrderDetailResDTO adminDetail(String orderId) {
        return orderDetail(orderId);
    }

    @Override
    public void adminCancel(String orderId) {
        Order order = orderMapper.queryByOrderId(orderId);
        if (order == null) {
            throw new CommonException(RespCode.ORDER_NOT_EXIST, "订单不存在");
        }
        Integer currentStatus = order.getStatus();
        if (currentStatus == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "订单状态异常");
        }

        // 允许取消：待支付(0)
        if (currentStatus != 0) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "当前状态不允许取消");
        }

        int updated = orderMapper.updateStatus(orderId, 0, 3);
        if (updated <= 0) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "取消失败，订单状态已变更");
        }
    }

    @Override
    public void adminRefund(String orderId) {
        Order order = orderMapper.queryByOrderId(orderId);
        if (order == null) {
            throw new CommonException(RespCode.ORDER_NOT_EXIST, "订单不存在");
        }
        Integer currentStatus = order.getStatus();
        if (currentStatus == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "订单状态异常");
        }

        // 允许退款/退票：已支付(1) 或 已出票(2)
        if (currentStatus != 1 && currentStatus != 2) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "当前状态不允许退票/退款");
        }

        int updated = orderMapper.updateStatus(orderId, currentStatus, 4);
        if (updated <= 0) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "退款失败，订单状态已变更");
        }
    }

    @Override
    public OrderStatisticsResDTO statistics(LocalDate startDate, LocalDate endDate, String groupBy) {
        LocalDate end = endDate;
        if (end == null) {
            end = LocalDate.now();
        }
        LocalDate start = startDate;
        if (start == null) {
            start = end.minusDays(6);
        }
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();

        List<Order> orders = orderMapper.findByCreateTimeRange(startTime, endTime);

        OrderStatisticsResDTO resDTO = new OrderStatisticsResDTO();

        OrderStatisticsResDTO.Overview overview = new OrderStatisticsResDTO.Overview();
        long totalOrders = orders.size();
        double totalAmountDouble = orders.stream()
                .map(Order::getAmount)
                .filter(a -> a != null)
                .mapToDouble(a -> a)
                .sum();
        long completedCount = orders.stream()
                .filter(o -> o.getStatus() != null && (o.getStatus() == 1 || o.getStatus() == 2))
                .count();

        BigDecimal totalAmount = BigDecimal.valueOf(totalAmountDouble);
        BigDecimal avgOrderAmount = totalOrders > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal completionRate = totalOrders > 0
                ? BigDecimal.valueOf(completedCount)
                        .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        overview.setTotalOrders(totalOrders);
        overview.setTotalAmount(totalAmount);
        overview.setAvgOrderAmount(avgOrderAmount);
        overview.setCompletionRate(completionRate);
        resDTO.setOverview(overview);

        Map<Integer, Long> statusCountMap = orders.stream()
                .filter(o -> o.getStatus() != null)
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        List<OrderStatisticsResDTO.StatusDistributionItem> statusItems = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : statusCountMap.entrySet()) {
            OrderStatisticsResDTO.StatusDistributionItem item = new OrderStatisticsResDTO.StatusDistributionItem();
            item.setStatus(entry.getKey());
            item.setName(getStatusName(entry.getKey()));
            item.setCount(entry.getValue());
            BigDecimal percentage = totalOrders > 0
                    ? BigDecimal.valueOf(entry.getValue() * 100.0 / totalOrders)
                            .setScale(2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;
            item.setPercentage(percentage);
            statusItems.add(item);
        }
        resDTO.setStatusDistribution(statusItems);

        Map<LocalDate, List<Order>> dateOrderMap = orders.stream()
                .filter(o -> o.getCreateTime() != null)
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate()));
        List<OrderStatisticsResDTO.TrendDataItem> trendItems = new ArrayList<>();
        dateOrderMap.keySet().stream().sorted().forEach(date -> {
            List<Order> dailyOrders = dateOrderMap.get(date);
            long count = dailyOrders.size();
            double amountSum = dailyOrders.stream()
                    .map(Order::getAmount)
                    .filter(a -> a != null)
                    .mapToDouble(a -> a)
                    .sum();
            OrderStatisticsResDTO.TrendDataItem item = new OrderStatisticsResDTO.TrendDataItem();
            item.setDate(date.toString());
            item.setOrderCount(count);
            item.setAmount(BigDecimal.valueOf(amountSum));
            trendItems.add(item);
        });
        resDTO.setTrendData(trendItems);

        return resDTO;
    }

    @Override
    public List<OrderListResDTO> recentOrders(Integer limit) {
        Integer queryLimit = limit;
        if (queryLimit == null || queryLimit <= 0) {
            queryLimit = 5;
        }

        List<Order> orders = orderMapper.findRecent(queryLimit);
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        List<OrderListResDTO> listResDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderListResDTO dto = BeanUtil.copyProperties(order, OrderListResDTO.class);
            listResDTOS.add(dto);
        }

        List<Long> ticketIds = listResDTOS.stream()
                .map(OrderListResDTO::getTicketId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (!ticketIds.isEmpty()) {
            List<ListTicketResDTO> ticketList = ticketClient.list(ticketIds);
            Map<Long, ListTicketResDTO> ticketMap = ticketList.stream()
                    .collect(Collectors.toMap(ListTicketResDTO::getId, t -> t));
            for (OrderListResDTO dto : listResDTOS) {
                ListTicketResDTO ticket = ticketMap.get(dto.getTicketId());
                if (ticket != null) {
                    dto.setTrainNumber(ticket.getTrainNumber());
                    dto.setStartStation(ticket.getStartStation());
                    dto.setEndStation(ticket.getEndStation());
                    dto.setStartTime(ticket.getStartTime());
                    dto.setEndTime(ticket.getEndTime());
                }
            }
        }

        List<Long> userIds = listResDTOS.stream()
                .map(OrderListResDTO::getUserId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        Map<Long, String> usernameMap = Map.of();
        if (!userIds.isEmpty()) {
            try {
                usernameMap = userClient.usernameList(userIds);
            } catch (Exception e) {
                log.error("获取用户名列表失败", e);
                usernameMap = Map.of();
            }
        }

        for (OrderListResDTO dto : listResDTOS) {
            if (dto.getUserId() != null && usernameMap.containsKey(dto.getUserId())) {
                dto.setUsername(usernameMap.get(dto.getUserId()));
            }
        }

        return listResDTOS;
    }

    @Override
    public List<OrderListResDTO> recentOrdersBase(Integer limit) {
        Integer queryLimit = limit;
        if (queryLimit == null || queryLimit <= 0) {
            queryLimit = 5;
        }

        List<Order> orders = orderMapper.findRecent(queryLimit);
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        List<OrderListResDTO> listResDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderListResDTO dto = BeanUtil.copyProperties(order, OrderListResDTO.class);
            listResDTOS.add(dto);
        }
        return listResDTOS;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已出票";
            case 3 -> "已取消";
            case 4 -> "已退票";
            case 5 -> "已改签";
            case 6 -> "已过期";
            default -> "未知";
        };
    }
}
