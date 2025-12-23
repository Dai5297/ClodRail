package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.dto.response.OrderListResDTO;
import com.rs.model.dto.response.OrderStatisticsResDTO;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.LocalTCC;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@LocalTCC
public interface OrderService {

    /**
     * 创建订单
     * 
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    @TwoPhaseBusinessAction(name = "createOrder", commitMethod = "commit", rollbackMethod = "rollback")
    OrderCreateResDTO createOrder(BusinessActionContext context,
            @BusinessActionContextParameter(paramName = "reqDTO") OrderCreateReqDTO reqDTO);

    /**
     * 创建订单TCC - 提交
     * 
     * @param context 上下文
     * @return 提交结果
     */
    boolean commit(BusinessActionContext context);

    /**
     * 创建订单TCC - 回滚
     * 
     * @param context 上下文
     * @return 回滚结果
     */
    boolean rollback(BusinessActionContext context);

    /**
     * 创建订单成功后续操作
     */
    void createOrderOnSuccess(CreateTicketOrderMessage orderMessage);

    /**
     * 创建非热门订单
     *
     * @param reqDTO 创建参数
     * @return 创建结果
     */
    OrderCreateResDTO commonCreateOrder(OrderCreateReqDTO reqDTO);

    /**
     * 订单详情
     *
     * @param orderId 订单id
     * @return 订单详情
     */
    OrderDetailResDTO orderDetail(String orderId);

    /**
     * 获取订单列表
     *
     * @return 订单列表
     */
    PageResult<OrderListResDTO> list(Integer pageNum, Integer pageSize, String orderId, Integer status);

    PageResult<OrderListResDTO> adminPage(Integer pageNum, Integer pageSize, String orderId, Integer status,
                                         Long userId, LocalDateTime startTime, LocalDateTime endTime);

    OrderDetailResDTO adminDetail(String orderId);

    void adminCancel(String orderId);

    void adminRefund(String orderId);

    /**
     * 订单统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param groupBy   分组方式
     * @return 统计结果
     */
    OrderStatisticsResDTO statistics(LocalDate startDate, LocalDate endDate, String groupBy);

    List<OrderListResDTO> recentOrders(Integer limit);

    List<OrderListResDTO> recentOrdersBase(Integer limit);
}
