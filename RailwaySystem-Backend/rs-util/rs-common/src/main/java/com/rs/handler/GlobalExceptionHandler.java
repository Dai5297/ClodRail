package com.rs.handler;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.exception.MqException;
import lombok.extern.slf4j.Slf4j;
import com.rs.model.RespResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler({CommonException.class, MqException.class})
    public RespResult handleCustomException(CommonException e) {
        log.error("自定义异常: code={}, message={}", e.getCode().getCode(), e.getMessage(), e);
        return RespResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RespResult handleException(Exception e) {
        log.error("系统异常: message={}", e.getMessage(), e);
        return RespResult.error(RespCode.SYSTEM_ERROR, "系统异常: " + e.getMessage());
    }
}
