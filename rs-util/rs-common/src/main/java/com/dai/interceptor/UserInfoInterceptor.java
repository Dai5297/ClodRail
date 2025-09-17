package com.dai.interceptor;

import cn.hutool.core.util.StrUtil;
import com.dai.constant.CommonConstant;
import com.dai.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(CommonConstant.USER_INFO);
        if (StrUtil.isNotBlank(header)) {
            UserContext.set(Long.valueOf(header));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
