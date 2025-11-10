package com.rs.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateTicketOrderMessage {
    private String orderId;

    private List<Long> passengerIds;
}
