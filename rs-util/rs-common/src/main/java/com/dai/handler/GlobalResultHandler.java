package com.dai.handler;

import com.dai.model.RespResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局结果封装处理（支持String类型）
 */
@ControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 判断是否需要处理
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 👇 排除 OpenAPI 文档路径
        String requestURI = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getRequestURI();

        if (requestURI.startsWith("/v3/api-docs") || requestURI.startsWith("/swagger")) {
            return false; // 不包装
        }

        return true;
    }

    /**
     * 处理方法返回结果
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 如果已经是RespResult类型，直接返回
        if (body instanceof RespResult) {
            return body;
        }

        // 处理String类型返回
        if (body instanceof String) {
            try {
                // 将RespResult对象转换为JSON字符串
                RespResult<Object> result = RespResult.ok(body);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON转换失败", e);
            }
        }

        // 如果body为null，返回成功的RespResult
        if (body == null) {
            return RespResult.ok();
        }

        // 包装成RespResult
        return RespResult.ok(body);
    }
}