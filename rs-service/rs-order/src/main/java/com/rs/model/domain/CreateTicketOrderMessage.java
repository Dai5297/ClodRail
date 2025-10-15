package com.rs.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateTicketOrderMessage {
    private Long orderId;

    private List<Long> passengerIds;
}
