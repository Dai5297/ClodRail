package com.rs.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderStatisticsResDTO {

    private Overview overview;

    private List<StatusDistributionItem> statusDistribution;

    private List<TrendDataItem> trendData;

    @Data
    public static class Overview {

        private Long totalOrders;

        private BigDecimal totalAmount;

        private BigDecimal avgOrderAmount;

        private BigDecimal completionRate;
    }

    @Data
    public static class StatusDistributionItem {

        private Integer status;

        private String name;

        private Long count;

        private BigDecimal percentage;
    }

    @Data
    public static class TrendDataItem {

        private String date;

        private Long orderCount;

        private BigDecimal amount;
    }
}

