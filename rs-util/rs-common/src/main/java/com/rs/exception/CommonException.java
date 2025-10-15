package com.rs.exception;

import com.rs.enums.RespCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException  {
    private final RespCode code;

    private final String message;

    public CommonException(RespCode code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CommonException(RespCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
