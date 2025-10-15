package com.rs.exception;

import lombok.Data;

@Data
public class MqException extends RuntimeException {
    /**
     * mq失败消息
     */
    private String msg;
    private Long mqId;

    public MqException() {
    }

    public MqException(Throwable throwable, String message) {
        super(message, throwable);
    }

    public MqException(String message) {
        super(message);
    }
}
