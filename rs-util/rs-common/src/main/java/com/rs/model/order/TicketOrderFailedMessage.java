package com.rs.model.order;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * ticket_order 下单业务消费者重试失败消息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TicketOrderFailedMessage extends BaseModel {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 消息唯一ID（建议使用业务全局ID或MQ Message ID）
     */
    private String messageId;

    /**
     * 关联的订单ID（若已生成）
     */
    private Long orderId;

    /**
     * 乘客ID（便于定位用户）
     */
    private List<Long> passengerId;

    /**
     * 座位ID（若已分配）
     */
    private List<Long> seatId;

    /**
     * 原始消息内容（JSON格式，包含下单请求完整上下文）
     */
    private String body;

    /**
     * 消费者重试次数
     */
    private Integer retryCount = 0;

    /**
     * 最终消费失败原因（异常堆栈或错误描述）
     */
    private String failReason;

    /**
     * 处理状态（0-未处理，1-已处理）
     */
    private Integer status = 0;
}
