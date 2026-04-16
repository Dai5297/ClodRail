package com.rs.handler;

import com.rs.model.RespResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHideInternalExceptionMessage() {
        RespResult result = handler.handleException(new RuntimeException("database exploded"));

        assertEquals(501, result.getCode());
        assertEquals("系统异常，请稍后重试", result.getMessage());
    }
}
