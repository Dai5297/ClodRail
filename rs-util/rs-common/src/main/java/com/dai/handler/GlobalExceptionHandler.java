package com.dai.handler;

import com.dai.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import com.dai.model.RespResult;
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
    @ExceptionHandler(CommonException.class)
    public RespResult handleCustomException(CommonException e) {
        log.error("自定义异常: code={}, message={}", e.getCode().getCode(), e.getMessage(), e);
        return RespResult.error(e.getCode(), e.getMessage());
    }
}
