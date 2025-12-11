package com.rs.mapper;

import com.rs.model.mall.MallOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商城订单Mapper
 */
@Mapper
public interface MallOrderMapper {

    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 影响行数
     */
    int createOrder(MallOrder order);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber 订单号
     * @return 订单信息
     */
    MallOrder findByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<MallOrder> findByUserId(@Param("userId") Long userId);

    /**
     * 更新订单状态
     *
     * @param orderNumber 订单号
     * @param status      状态
     * @return 影响行数
     */
    int updateStatus(@Param("orderNumber") String orderNumber, @Param("status") Integer status);

    /**
     * 分页查询用户商城订单
     *
     * @param userId 用户ID
     * @param status 订单状态（0-待发货 1-已发货 2-已完成 3-已取消）
     * @return 订单列表
     */
    List<MallOrder> queryUserOrders(@Param("userId") Long userId, @Param("status") Integer status);
}
