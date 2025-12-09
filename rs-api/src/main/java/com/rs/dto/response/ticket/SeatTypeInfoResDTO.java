package com.rs.dto.response.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SeatTypeInfoResDTO {

    private String orderId;

    private List<SeatInfoResDTO> seatInfo;
}
