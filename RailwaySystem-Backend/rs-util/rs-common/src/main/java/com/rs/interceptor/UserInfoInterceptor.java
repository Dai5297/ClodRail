package com.rs.interceptor;

import com.rs.constant.CommonConstant;
import com.rs.util.JWTUtil;
import com.rs.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (requestUri != null
                && requestUri.startsWith(CommonConstant.USER_PATH_PREFIX)
                && !requestUri.startsWith(CommonConstant.USER_PATH_PREFIX + "/auth")) {
            String authorization = request.getHeader(CommonConstant.AUTHENTICATION);
            if (authorization == null
                    || !authorization.startsWith(CommonConstant.AUTH_PREFIX)
                    || authorization.length() <= CommonConstant.AUTH_PREFIX.length()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
                return false;
            }
            try {
                String accessToken = authorization.substring(CommonConstant.AUTH_PREFIX.length());
                Claims claims = JWTUtil.parseJWT(accessToken);
                if (!JWTUtil.isAccessToken(claims)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
                    return false;
                }
                Long userId = JWTUtil.getUserId(claims);
                if (userId == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
                    return false;
                }
                UserContext.set(userId);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
                return false;
            }
            return true;
        }

        // 非 /customer/** 用户端路径暂时保持兼容逻辑，本次不收口 B 端与其他内部路径的 user-info 头来源。
        String header = request.getHeader(CommonConstant.USER_INFO);
        if (header != null && !header.isBlank()) {
            UserContext.set(Long.valueOf(header));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
