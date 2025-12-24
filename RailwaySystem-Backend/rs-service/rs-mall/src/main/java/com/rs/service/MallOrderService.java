package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.dto.req.ExchangeReqDTO;
import com.rs.model.dto.res.ExchangeResDTO;
import com.rs.model.dto.res.MallOrderResDTO;

/**
 * 商城订单服务
 */
public interface MallOrderService {

    /**
     * 商品兑换下单
     *
     * @param exchangeReqDTO 兑换请求
     * @return 兑换结果
     */
    ExchangeResDTO exchange(ExchangeReqDTO exchangeReqDTO);

    /**
     * 查询用户商城订单
     *
     * @param page   页码
     * @param size   每页数量
     * @param status 订单状态
     * @return 订单列表
     */
    PageResult<MallOrderResDTO> getUserOrders(Integer page, Integer size, Integer status);

    PageResult<MallOrderResDTO> adminPage(String orderNumber, Long userId, Integer status, Integer pageNum, Integer pageSize);

    void adminShip(String orderNumber);
}
