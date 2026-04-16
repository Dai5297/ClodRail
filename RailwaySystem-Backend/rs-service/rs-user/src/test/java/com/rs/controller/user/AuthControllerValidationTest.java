package com.rs.controller.user;

import com.rs.handler.GlobalExceptionHandler;
import com.rs.model.dto.response.user.UserLoginResDTO;
import com.rs.properties.AuthProperties;
import com.rs.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerValidationTest {

    private MockMvc mockMvc;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        userAuthService = mock(UserAuthService.class);
        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtKey("test-jwt-key-1234567890-abcdef123456");
        authProperties.setRefreshCookieSecure(true);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(userAuthService, authProperties))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void loginShouldRejectBlankUsername() throws Exception {
        mockMvc.perform(post("/customer/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));

        verifyNoInteractions(userAuthService);
    }

    @Test
    void registerShouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(post("/customer/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"tester",
                                  "password":"password123",
                                  "email":"bad-email",
                                  "phone":"13800138000",
                                  "realName":"Test User",
                                  "idCard":"110101199001011234",
                                  "code":"123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));

        verifyNoInteractions(userAuthService);
    }

    @Test
    void changeEmailShouldRejectInvalidEmailInRequestBody() throws Exception {
        mockMvc.perform(post("/customer/auth/email/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newEmail":"bad-email","code":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));

        verifyNoInteractions(userAuthService);
    }

    @Test
    void loginShouldWriteSecureRefreshTokenCookieWhenConfigured() throws Exception {
        UserLoginResDTO response = new UserLoginResDTO();
        response.setRefreshToken("refresh-token-value");
        response.setAccessToken("access-token-value");
        response.setToken("access-token-value");
        when(userAuthService.userLoginByUserName(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/customer/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"tester","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Secure")))
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
    }
}
