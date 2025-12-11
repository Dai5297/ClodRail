package com.rs.service.impl;

import com.github.pagehelper.Page;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.ItemMapper;
import com.rs.mapper.MallOrderMapper;
import com.rs.mapper.PointMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.req.ExchangeReqDTO;
import com.rs.model.dto.res.ExchangeResDTO;
import com.rs.model.dto.res.MallOrderResDTO;
import com.rs.model.mall.Item;
import com.rs.model.mall.MallOrder;
import com.rs.model.mall.PointBalance;
import com.rs.model.mall.PointDetail;
import com.rs.service.MallOrderService;
import com.rs.util.PageUtil;
import com.rs.util.RedisIdUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商城订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallOrderServiceImpl implements MallOrderService {

    private final MallOrderMapper mallOrderMapper;
    private final ItemMapper itemMapper;
    private final PointMapper pointMapper;

    private final RedisIdUtil redisIdUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeResDTO exchange(ExchangeReqDTO exchangeReqDTO) {
        // 1. 获取当前用户ID
        Long userId = UserContext.get();
        if (userId == null) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }

        // 2. 查询商品信息
        Item item = itemMapper.getItemById(exchangeReqDTO.getItemId());
        if (item == null) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "商品不存在");
        }
        if (item.getStatus() != 1) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "商品已下架");
        }
        if (item.getStock() < exchangeReqDTO.getQuantity()) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "商品库存不足");
        }

        // 3. 计算总积分
        Integer totalPoints = item.getPrice() * exchangeReqDTO.getQuantity();

        // 4. 查询用户积分余额
        PointBalance pointBalance = pointMapper.getPointBalance(userId);
        if (pointBalance == null || pointBalance.getCurrentPoints() < totalPoints) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT,"积分不足");
        }

        // 5. 扣减库存（乐观锁）
        int stockResult = itemMapper.deductStock(exchangeReqDTO.getItemId(), exchangeReqDTO.getQuantity());
        if (stockResult == 0) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT,"库存扣减失败，商品可能已售罄");
        }

        // 6. 增加销量
        itemMapper.increaseSold(exchangeReqDTO.getItemId(), exchangeReqDTO.getQuantity());

        // 7. 扣减积分（乐观锁）
        int pointResult = pointMapper.deductPoints(userId, totalPoints);
        if (pointResult == 0) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT,"积分扣减失败，积分可能不足");
        }

        // 8. 添加积分消费明细
        PointDetail pointDetail = new PointDetail();
        pointDetail.setUserId(userId);
        pointDetail.setType(2); // 2-消费
        pointDetail.setPoint(totalPoints);
        pointDetail.setComment("兑换商品：" + item.getName());
        pointDetail.setCreateBy(userId);
        pointDetail.setUpdateBy(userId);
        pointMapper.addSpendDetail(pointDetail);

        // 9. 创建订单
        MallOrder order = new MallOrder();
        order.setOrderNumber(redisIdUtil.nextId("icr:mall:order" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))));
        order.setUserId(userId);
        order.setItemId(item.getId());
        order.setItemName(item.getName());
        order.setItemImage(item.getImage());
        order.setQuantity(exchangeReqDTO.getQuantity());
        order.setUnitPrice(item.getPrice());
        order.setTotalPoints(totalPoints);
        order.setRecipientName(exchangeReqDTO.getRecipientName());
        order.setRecipientPhone(exchangeReqDTO.getRecipientPhone());
        order.setRecipientAddress(exchangeReqDTO.getRecipientAddress());
        order.setStatus(0); // 0-待发货
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setCreateBy(userId);
        order.setUpdateBy(userId);

        mallOrderMapper.createOrder(order);

        // 10. 查询剩余积分
        PointBalance updatedBalance = pointMapper.getPointBalance(userId);
        Long remainingPoints = updatedBalance.getCurrentPoints();

        log.info("用户 {} 成功兑换商品 {}, 订单号: {}, 消费积分: {}, 剩余积分: {}",
                userId, item.getName(), order.getOrderNumber(), totalPoints, remainingPoints);

        return new ExchangeResDTO(true, order.getOrderNumber(), remainingPoints);
    }

    @Override
    public PageResult<MallOrderResDTO> getUserOrders(Integer page, Integer size, Integer status) {
        // 获取当前用户ID
        Long userId = UserContext.get();
        if (userId == null) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }

        // 分页查询
        PageUtil.startPage(page, size);
        List<MallOrder> orders = mallOrderMapper.queryUserOrders(userId, status);

        // 转换为DTO
        List<MallOrderResDTO> records = orders.stream().map(order -> {
            MallOrderResDTO dto = new MallOrderResDTO();
            dto.setOrderNumber(order.getOrderNumber());
            dto.setItemId(order.getItemId());
            dto.setItemName(order.getItemName());
            dto.setItemImage(order.getItemImage());
            dto.setQuantity(order.getQuantity());
            dto.setUnitPrice(order.getUnitPrice());
            dto.setTotalPoints(order.getTotalPoints());
            dto.setRecipientName(order.getRecipientName());
            dto.setRecipientPhone(order.getRecipientPhone());
            dto.setRecipientAddress(order.getRecipientAddress());
            dto.setStatus(order.getStatus());
            dto.setStatusText(getStatusText(order.getStatus()));
            dto.setShipTime(order.getShipTime());
            dto.setCompleteTime(order.getCompleteTime());
            dto.setCreateTime(order.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return PageUtil.buildPageResultFromSource((Page<?>) orders, records);
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待发货";
            case 1: return "已发货";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
