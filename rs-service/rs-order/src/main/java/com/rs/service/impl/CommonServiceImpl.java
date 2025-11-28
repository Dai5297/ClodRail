package com.rs.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.model.order.Order;
import com.rs.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.rs.constant.RedisKeyConstant.ASSISTANT_ORDER;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String generateAssistantOrder(Order order) {
        String uuid = UUID.randomUUID().toString();
        String key = ASSISTANT_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + uuid;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(order));
        return uuid;
    }

    @Override
    public Order praseAssistantOrder(String uuid) {
        String key = ASSISTANT_ORDER + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")) + uuid;
        String orderStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(orderStr)) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "未知链接");
        }
        return JSONUtil.toBean(orderStr, Order.class);
    }
}
