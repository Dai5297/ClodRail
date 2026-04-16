package com.rs.interceptor;

import com.rs.util.JWTUtil;
import com.rs.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static com.rs.constant.CommonConstant.ADMIN_PATH_PREFIX;
import static com.rs.constant.CommonConstant.AUTHENTICATION;
import static com.rs.constant.CommonConstant.AUTH_PREFIX;
import static com.rs.constant.CommonConstant.USER_INFO;
import static com.rs.constant.CommonConstant.USER_PATH_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInfoInterceptorTest {

    private final UserInfoInterceptor interceptor = new UserInfoInterceptor();

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void customerRequestShouldUseAccessToken() throws Exception {
        JWTUtil.initialize("test-jwt-key-1234567890-abcdef123456");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(USER_PATH_PREFIX + "/orders");
        request.addHeader(AUTHENTICATION, AUTH_PREFIX + JWTUtil.createAccessToken(123L, 60_000L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
        assertEquals(123L, UserContext.get());
    }

    @Test
    void customerRequestShouldRejectMissingOrInvalidAccessToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(USER_PATH_PREFIX + "/orders");
        request.addHeader(AUTHENTICATION, AUTH_PREFIX + "invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertEquals(401, response.getStatus());
        assertNull(UserContext.get());
    }

    @Test
    void adminRequestCanStillUseGatewayInjectedUserInfo() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(ADMIN_PATH_PREFIX + "/orders");
        request.addHeader(USER_INFO, "456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
        assertEquals(456L, UserContext.get());
    }
}
