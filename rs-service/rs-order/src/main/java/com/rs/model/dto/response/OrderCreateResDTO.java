package com.rs.model.dto.response;

import com.rs.model.domain.Passenger;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateResDTO {

    private String id;

    private Double amount;

    private Integer status;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private List<Passenger> passengers;
}
