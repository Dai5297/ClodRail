package com.rs.dto.response.ticket;

import lombok.Data;

@Data
public class FetchSeatResDTO {

    private Long id;

    private String seatNo;

    private String fullSeatCode;
}
