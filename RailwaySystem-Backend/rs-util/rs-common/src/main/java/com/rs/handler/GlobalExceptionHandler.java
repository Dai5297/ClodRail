package com.rs.handler;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.exception.MqException;
import lombok.extern.slf4j.Slf4j;
import com.rs.model.RespResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.ConstraintViolationException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "请求参数不合法";
        return RespResult.error(RespCode.DATA_NOT_CONSISTENT, message);
    }

    @ExceptionHandler(BindException.class)
    public RespResult handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "请求参数不合法";
        return RespResult.error(RespCode.DATA_NOT_CONSISTENT, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public RespResult handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("请求参数不合法");
        return RespResult.error(RespCode.DATA_NOT_CONSISTENT, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public RespResult handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数不合法");
        return RespResult.error(RespCode.DATA_NOT_CONSISTENT, message);
    }

    @ExceptionHandler(Exception.class)
    public RespResult handleException(Exception e) {
        log.error("系统异常: message={}", e.getMessage(), e);
        return RespResult.error(RespCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }
}
