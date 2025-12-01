package com.rs.mapper;

import com.rs.model.domain.Permissions;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.order.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 是否创建成功
     */
    boolean createOrder(Order order);

    /**
     * 创建订单座位关系
     *
     * @param orderId 订单ID
     * @param seatId  座位ID
     */
    void createOrderSeat(String orderId, Long seatId, Long passengerId);

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailResDTO queryDetail(String orderId);

    /**
     * 查询订单乘客信息
     *
     * @param orderId 订单ID
     * @return 订单乘客信息
     */
    List<Long> queryPassengers(String orderId);

    /**
     * 查询订单权限信息
     *
     * @param orderId 订单ID
     * @return 订单权限信息
     */
    Permissions queryPermission(String orderId);

    /**
     * 更新订单支付状态
     *
     * @param orderId 订单ID
     */
    void updateAliPayStatus(String orderId);

    /**
     * 根据订单ID查询订单信息
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order queryByOrderId(String orderId);

    /**
     * 查询订单列表
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param status  订单状态
     * @return 订单列表
     */
    List<OrderDetailResDTO> queryOrders(Long userId, String orderId, Integer status);

    /**
     * 根据ID查询订单信息
     *
     * @param id 订单ID
     * @return 订单信息
     */
    Order queryById(Long id);

    /**
     * 根据用户ID查询订单信息
     *
     * @param userId 用户ID
     * @return 订单信息
     */
    List<Order> findByUserId(Long userId);
}
