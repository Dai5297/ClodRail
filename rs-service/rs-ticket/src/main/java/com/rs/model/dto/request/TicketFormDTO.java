package com.rs.model.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketFormDTO {
    /**
     * ID (Update only)
     */
    private Long id;

    /**
     * Line ID
     */
    private Long lineId;

    /**
     * Train ID
     */
    private Long trainId;

    /**
     * Start Time
     */
    private LocalDateTime startTime;

    /**
     * End Time
     */
    private LocalDateTime endTime;

    /**
     * First Class Price
     */
    private Double firstClassPrice;

    /**
     * Second Class Price
     */
    private Double secondClassPrice;

    /**
     * Status: 0-Not on sale, 1-On sale, 2-Sold out
     */
    private String status;

    /**
     * Is Hot: 0-No, 1-Yes
     */
    private Integer isHot;
}
