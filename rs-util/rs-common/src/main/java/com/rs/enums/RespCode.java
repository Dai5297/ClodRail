package com.rs.enums;

import lombok.Getter;

@Getter
public enum RespCode {

    SUCCESS(200),
    UNAUTHORIZED(401),
    ERROR(500),
    SYSTEM_ERROR(501),
    DATA_NOT_EXIST(502),
    DATA_NOT_CONSISTENT(503),
    TICKET_SEAT_NOT_ENOUGH(1000),
    TICKET_ORDER_CREATE_FAIL(1001),
    ORDER_NOT_EXIST(1002);

    private Integer code;

    RespCode() {
    }

    RespCode(Integer code) {
        this.code = code;
    }

}
