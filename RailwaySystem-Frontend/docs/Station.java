package com.railway.entity;

import java.time.LocalDateTime;

/**
 * 车站实体类
 * 基于车票查询和预订页面的车站信息需求分析
 */
public class Station {
    
    /**
     * 车站ID
     */
    private Long stationId;
    
    /**
     * 车站代码 (如: BJP-北京, SHH-上海虹桥)
     */
    private String stationCode;
    
    /**
     * 车站名称
     */
    private String stationName;
    
    /**
     * 车站简称
     */
    private String shortName;
    
    /**
     * 车站拼音
     */
    private String pinyin;
    
    /**
     * 车站拼音首字母
     */
    private String pinyinInitial;
    
    /**
     * 所属城市
     */
    private String city;
    
    /**
     * 所属省份
     */
    private String province;
    
    /**
     * 车站类型 (特等站/一等站/二等站/三等站/四等站/五等站)
     */
    private String stationType;
    
    /**
     * 车站等级 (1-5级)
     */
    private Integer stationLevel;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 车站地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 是否支持高铁
     */
    private Boolean supportHighSpeed;
    
    /**
     * 是否支持动车
     */
    private Boolean supportEmu;
    
    /**
     * 是否支持普速列车
     */
    private Boolean supportRegular;
    
    /**
     * 是否为热门车站
     */
    private Boolean isPopular;
    
    /**
     * 车站状态 (正常/维护中/停用)
     */
    private String status;
    
    /**
     * 排序权重 (用于搜索结果排序)
     */
    private Integer sortWeight;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public Station() {}
    
    public Station(String stationCode, String stationName, String city, String province) {
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.city = city;
        this.province = province;
        this.status = "正常";
        this.isPopular = false;
        this.supportHighSpeed = true;
        this.supportEmu = true;
        this.supportRegular = true;
        this.sortWeight = 0;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getStationId() {
        return stationId;
    }
    
    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
    
    public String getStationCode() {
        return stationCode;
    }
    
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    
    public String getStationName() {
        return stationName;
    }
    
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    public String getPinyin() {
        return pinyin;
    }
    
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
    
    public String getPinyinInitial() {
        return pinyinInitial;
    }
    
    public void setPinyinInitial(String pinyinInitial) {
        this.pinyinInitial = pinyinInitial;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getStationType() {
        return stationType;
    }
    
    public void setStationType(String stationType) {
        this.stationType = stationType;
    }
    
    public Integer getStationLevel() {
        return stationLevel;
    }
    
    public void setStationLevel(Integer stationLevel) {
        this.stationLevel = stationLevel;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Boolean getSupportHighSpeed() {
        return supportHighSpeed;
    }
    
    public void setSupportHighSpeed(Boolean supportHighSpeed) {
        this.supportHighSpeed = supportHighSpeed;
    }
    
    public Boolean getSupportEmu() {
        return supportEmu;
    }
    
    public void setSupportEmu(Boolean supportEmu) {
        this.supportEmu = supportEmu;
    }
    
    public Boolean getSupportRegular() {
        return supportRegular;
    }
    
    public void setSupportRegular(Boolean supportRegular) {
        this.supportRegular = supportRegular;
    }
    
    public Boolean getIsPopular() {
        return isPopular;
    }
    
    public void setIsPopular(Boolean isPopular) {
        this.isPopular = isPopular;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getSortWeight() {
        return sortWeight;
    }
    
    public void setSortWeight(Integer sortWeight) {
        this.sortWeight = sortWeight;
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
     * 获取车站全名 (城市 + 车站名)
     * @return 车站全名
     */
    public String getFullName() {
        if (city != null && !city.equals(stationName)) {
            return city + stationName;
        }
        return stationName;
    }
    
    /**
     * 检查是否支持指定类型的列车
     * @param trainType 列车类型 (G/D/C/K/T等)
     * @return true表示支持
     */
    public boolean supportsTrainType(String trainType) {
        if (trainType == null) return false;
        
        switch (trainType.toUpperCase()) {
            case "G":
                return supportHighSpeed;
            case "D":
            case "C":
                return supportEmu;
            case "K":
            case "T":
            case "Z":
                return supportRegular;
            default:
                return true;
        }
    }
    
    /**
     * 检查车站是否正常运营
     * @return true表示正常运营
     */
    public boolean isOperational() {
        return "正常".equals(status);
    }
    
    /**
     * 获取车站显示名称 (优先显示简称)
     * @return 显示名称
     */
    public String getDisplayName() {
        return shortName != null && !shortName.isEmpty() ? shortName : stationName;
    }
    
    @Override
    public String toString() {
        return "Station{" +
                "stationId=" + stationId +
                ", stationCode='" + stationCode + '\'' +
                ", stationName='" + stationName + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", stationType='" + stationType + '\'' +
                ", isPopular=" + isPopular +
                ", status='" + status + '\'' +
                '}';
    }
}