package com.railway.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 车票实体类
 * 基于车票查询列表页面和详情页面的需求分析
 */
public class Ticket {
    
    /**
     * 车票ID
     */
    private Long ticketId;
    
    /**
     * 车次号 (如: G123, D311, K456)
     */
    private String trainNumber;
    
    /**
     * 列车类型 (高铁/动车/快车/普快等)
     */
    private String trainType;
    
    /**
     * 出发站ID
     */
    private Long departureStationId;
    
    /**
     * 出发站名称
     */
    private String departureStationName;
    
    /**
     * 到达站ID
     */
    private Long arrivalStationId;
    
    /**
     * 到达站名称
     */
    private String arrivalStationName;
    
    /**
     * 出发时间
     */
    private LocalTime departureTime;
    
    /**
     * 到达时间
     */
    private LocalTime arrivalTime;
    
    /**
     * 出发日期
     */
    private LocalDateTime departureDate;
    
    /**
     * 到达日期
     */
    private LocalDateTime arrivalDate;
    
    /**
     * 运行时长(分钟)
     */
    private Integer duration;
    
    /**
     * 座位类型 (一等座/二等座/商务座/硬卧/软卧等)
     */
    private String seatType;
    
    /**
     * 票价
     */
    private BigDecimal price;
    
    /**
     * 余票数量
     */
    private Integer availableSeats;
    
    /**
     * 车票状态 (有票/余票不足/已售完)
     */
    private String ticketStatus;
    
    /**
     * 座位号 (预订后分配)
     */
    private String seatNumber;
    
    /**
     * 车厢号
     */
    private String carriageNumber;
    
    /**
     * 是否可预订
     */
    private Boolean bookable;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public Ticket() {}
    
    public Ticket(String trainNumber, String trainType, String departureStationName, 
                  String arrivalStationName, LocalTime departureTime, LocalTime arrivalTime,
                  String seatType, BigDecimal price, Integer availableSeats) {
        this.trainNumber = trainNumber;
        this.trainType = trainType;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatType = seatType;
        this.price = price;
        this.availableSeats = availableSeats;
        this.bookable = availableSeats > 0;
        this.ticketStatus = availableSeats > 0 ? (availableSeats > 10 ? "有票" : "余票不足") : "已售完";
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
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
    
    public String getTrainType() {
        return trainType;
    }
    
    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }
    
    public Long getDepartureStationId() {
        return departureStationId;
    }
    
    public void setDepartureStationId(Long departureStationId) {
        this.departureStationId = departureStationId;
    }
    
    public String getDepartureStationName() {
        return departureStationName;
    }
    
    public void setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
    }
    
    public Long getArrivalStationId() {
        return arrivalStationId;
    }
    
    public void setArrivalStationId(Long arrivalStationId) {
        this.arrivalStationId = arrivalStationId;
    }
    
    public String getArrivalStationName() {
        return arrivalStationName;
    }
    
    public void setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
    }
    
    public LocalTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public LocalDateTime getDepartureDate() {
        return departureDate;
    }
    
    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }
    
    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }
    
    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getSeatType() {
        return seatType;
    }
    
    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
        // 自动更新票务状态和可预订状态
        this.bookable = availableSeats > 0;
        this.ticketStatus = availableSeats > 0 ? (availableSeats > 10 ? "有票" : "余票不足") : "已售完";
    }
    
    public String getTicketStatus() {
        return ticketStatus;
    }
    
    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
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
    
    public Boolean getBookable() {
        return bookable;
    }
    
    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
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
    
    /**
     * 获取格式化的运行时长
     * @return 格式化的时长字符串 (如: "5小时42分")
     */
    public String getFormattedDuration() {
        if (duration == null) return "";
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "小时" + minutes + "分";
    }
    
    /**
     * 检查是否为当日到达
     * @return true表示当日到达，false表示次日到达
     */
    public boolean isSameDayArrival() {
        if (departureDate == null || arrivalDate == null) return true;
        return departureDate.toLocalDate().equals(arrivalDate.toLocalDate());
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainType='" + trainType + '\'' +
                ", departureStationName='" + departureStationName + '\'' +
                ", arrivalStationName='" + arrivalStationName + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", seatType='" + seatType + '\'' +
                ", price=" + price +
                ", availableSeats=" + availableSeats +
                ", ticketStatus='" + ticketStatus + '\'' +
                '}';
    }
}