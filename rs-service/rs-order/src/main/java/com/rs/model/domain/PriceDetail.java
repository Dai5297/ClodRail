package com.rs.model.domain;

import lombok.Data;

import java.util.List;

@Data
public class PriceDetail {

    private Double baseAmount;

    private Double discountAmount;

    private Double totalAmount;

    private List<Breakdown> breakdown;

    private Long voucherId;

    @Data
    public static class Breakdown {
        private String passengerName;

        private Integer PassengerType;

        private Double basePrice;

        private Double discount;

        private Double actualPrice;
    }
}
