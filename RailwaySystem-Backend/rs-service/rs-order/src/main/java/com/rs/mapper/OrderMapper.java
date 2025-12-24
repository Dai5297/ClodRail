package com.rs.mapper;

import com.rs.model.domain.Permissions;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.order.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    int updateStatus(@Param("orderId") String orderId,
                     @Param("expectStatus") Integer expectStatus,
                     @Param("status") Integer status);

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
    List<Order> findByUserId(
            @Param("userId") Long userId,
            @Param("orderId") String orderId,
            @Param("status") Integer status
    );

    /**
     * 查询订单座位信息
     *
     * @param orderId 订单ID
     * @return 订单座位信息
     */
    List<String> querySeat(String orderId);

    Long querySeatIdByPassenger(@Param("orderId") String orderId,
                                @Param("passengerId") Long passengerId);

    List<Order> findRecent(@Param("limit") Integer limit);

    /**
     * 根据创建时间范围查询订单
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 订单列表
     */
    List<Order> findByCreateTimeRange(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    List<Order> findAdminPage(@Param("orderId") String orderId,
                              @Param("status") Integer status,
                              @Param("userId") Long userId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    /**
     * 扫描过期未支付订单
     *
     * @param now   当前时间
     * @param limit 扫描数量限制
     * @return 过期未支付订单ID列表
     */
    List<String> findExpiredUnpaidOrderIds(@Param("now") LocalDateTime now,
                                           @Param("limit") Integer limit);

    /**
     * 批量标记订单过期
     *
     * @param orderIds       订单ID列表
     * @param expiredStatus 过期状态
     * @return 更新数量
     */
    int markOrdersExpired(@Param("orderIds") List<String> orderIds,
                           @Param("expiredStatus") Integer expiredStatus);

    List<String> findOrderIdsByIdsAndStatus(@Param("orderIds") List<String> orderIds,
                                           @Param("status") Integer status);
}
