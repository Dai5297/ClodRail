package com.railway.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 * 基于车票预订页面和订单管理需求分析
 */
public class Order {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单号 (唯一标识)
     */
    private String orderNumber;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 车票ID
     */
    private Long ticketId;
    
    /**
     * 车次号
     */
    private String trainNumber;
    
    /**
     * 出发站名称
     */
    private String departureStationName;
    
    /**
     * 到达站名称
     */
    private String arrivalStationName;
    
    /**
     * 出发时间
     */
    private LocalDateTime departureTime;
    
    /**
     * 到达时间
     */
    private LocalDateTime arrivalTime;
    
    /**
     * 乘车人姓名
     */
    private String passengerName;
    
    /**
     * 乘车人身份证号
     */
    private String passengerIdCard;
    
    /**
     * 乘车人手机号
     */
    private String passengerPhone;
    
    /**
     * 乘车人类型 (成人/儿童/学生)
     */
    private String passengerType;
    
    /**
     * 座位类型
     */
    private String seatType;
    
    /**
     * 座位号
     */
    private String seatNumber;
    
    /**
     * 车厢号
     */
    private String carriageNumber;
    
    /**
     * 票数量
     */
    private Integer ticketCount;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 保险费用
     */
    private BigDecimal insuranceFee;
    
    /**
     * 是否购买保险
     */
    private Boolean hasInsurance;
    
    /**
     * 订单状态 (待支付/已支付/已出票/已取消/已退票/改签中)
     */
    private String orderStatus;
    
    /**
     * 支付状态 (未支付/支付中/已支付/支付失败/已退款)
     */
    private String paymentStatus;
    
    /**
     * 支付方式 (支付宝/微信/银行卡)
     */
    private String paymentMethod;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 支付流水号
     */
    private String paymentTransactionId;
    
    /**
     * 电子客票号
     */
    private String eTicketNumber;
    
    /**
     * 退票时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 退票金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退票手续费
     */
    private BigDecimal refundFee;
    
    /**
     * 改签次数
     */
    private Integer changeCount;
    
    /**
     * 最后改签时间
     */
    private LocalDateTime lastChangeTime;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 订单过期时间 (未支付订单的过期时间)
     */
    private LocalDateTime expireTime;
    
    // 构造函数
    public Order() {}
    
    public Order(Long userId, Long ticketId, String trainNumber, String passengerName, 
                 String passengerIdCard, String seatType, BigDecimal unitPrice, Integer ticketCount) {
        this.userId = userId;
        this.ticketId = ticketId;
        this.trainNumber = trainNumber;
        this.passengerName = passengerName;
        this.passengerIdCard = passengerIdCard;
        this.seatType = seatType;
        this.unitPrice = unitPrice;
        this.ticketCount = ticketCount;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(ticketCount));
        this.orderStatus = "待支付";
        this.paymentStatus = "未支付";
        this.hasInsurance = false;
        this.insuranceFee = BigDecimal.ZERO;
        this.changeCount = 0;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        // 设置订单过期时间为创建后30分钟
        this.expireTime = LocalDateTime.now().plusMinutes(30);
        // 生成订单号
        this.orderNumber = generateOrderNumber();
    }
    
    // Getter和Setter方法
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    
    public String getTrainNumber() {
        return trainNumber;
    }
    
    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }
    
    public String getDepartureStationName() {
        return departureStationName;
    }
    
    public void setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
    }
    
    public String getArrivalStationName() {
        return arrivalStationName;
    }
    
    public void setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getPassengerName() {
        return passengerName;
    }
    
    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
    
    public String getPassengerIdCard() {
        return passengerIdCard;
    }
    
    public void setPassengerIdCard(String passengerIdCard) {
        this.passengerIdCard = passengerIdCard;
    }
    
    public String getPassengerPhone() {
        return passengerPhone;
    }
    
    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }
    
    public String getPassengerType() {
        return passengerType;
    }
    
    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }
    
    public String getSeatType() {
        return seatType;
    }
    
    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public String getCarriageNumber() {
        return carriageNumber;
    }
    
    public void setCarriageNumber(String carriageNumber) {
        this.carriageNumber = carriageNumber;
    }
    
    public Integer getTicketCount() {
        return ticketCount;
    }
    
    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
        // 自动更新总金额
        if (this.unitPrice != null) {
            this.totalAmount = this.unitPrice.multiply(BigDecimal.valueOf(ticketCount));
        }
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // 自动更新总金额
        if (this.ticketCount != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(this.ticketCount));
        }
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getInsuranceFee() {
        return insuranceFee;
    }
    
    public void setInsuranceFee(BigDecimal insuranceFee) {
        this.insuranceFee = insuranceFee;
    }
    
    public Boolean getHasInsurance() {
        return hasInsurance;
    }
    
    public void setHasInsurance(Boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
        // 如果不购买保险，保险费用设为0
        if (!hasInsurance) {
            this.insuranceFee = BigDecimal.ZERO;
        }
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }
    
    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }
    
    public String getETicketNumber() {
        return eTicketNumber;
    }
    
    public void setETicketNumber(String eTicketNumber) {
        this.eTicketNumber = eTicketNumber;
    }
    
    public LocalDateTime getRefundTime() {
        return refundTime;
    }
    
    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public BigDecimal getRefundFee() {
        return refundFee;
    }
    
    public void setRefundFee(BigDecimal refundFee) {
        this.refundFee = refundFee;
    }
    
    public Integer getChangeCount() {
        return changeCount;
    }
    
    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }
    
    public LocalDateTime getLastChangeTime() {
        return lastChangeTime;
    }
    
    public void setLastChangeTime(LocalDateTime lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        return "RW" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
    
    /**
     * 获取订单最终金额 (包含保险费)
     * @return 最终金额
     */
    public BigDecimal getFinalAmount() {
        BigDecimal finalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        if (hasInsurance && insuranceFee != null) {
            finalAmount = finalAmount.add(insuranceFee);
        }
        return finalAmount;
    }
    
    /**
     * 检查订单是否已过期
     * @return true表示已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime) && "待支付".equals(orderStatus);
    }
    
    /**
     * 检查订单是否可以退票
     * @return true表示可以退票
     */
    public boolean canRefund() {
        return "已支付".equals(orderStatus) || "已出票".equals(orderStatus);
    }
    
    /**
     * 检查订单是否可以改签
     * @return true表示可以改签
     */
    public boolean canChange() {
        return ("已支付".equals(orderStatus) || "已出票".equals(orderStatus)) && changeCount < 2;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", departureStationName='" + departureStationName + '\'' +
                ", arrivalStationName='" + arrivalStationName + '\'' +
                ", seatType='" + seatType + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}