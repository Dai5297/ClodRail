package com.rs.controller.user;

import com.rs.model.dto.request.user.*;
import com.rs.model.dto.response.user.UserLoginResDTO;
import com.rs.model.dto.response.user.UserRefreshTokenResDTO;
import com.rs.model.dto.response.user.UserRegisterResDTO;
import com.rs.properties.AuthProperties;
import com.rs.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.rs.constant.CommonConstant.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/customer/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "账号相关接口")
public class AuthController {

    private final UserAuthService userAuthService;
    private final AuthProperties authProperties;

    @PostMapping("/login/username")
    @Operation(summary = "用户名密码登录")
    public UserLoginResDTO loginByUserName(@Valid @RequestBody UserNameLoginReqDTO reqDTO, HttpServletResponse response) {
        UserLoginResDTO loginResDTO = userAuthService.userLoginByUserName(reqDTO);
        writeRefreshTokenCookie(response, loginResDTO.getRefreshToken(), 7 * 24 * 60 * 60);
        return loginResDTO;
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public void logout(@CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
                       HttpServletResponse response) {
        userAuthService.userLogout(refreshToken);
        clearRefreshTokenCookie(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 access token")
    public UserRefreshTokenResDTO refresh(@CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
                                          HttpServletResponse response) {
        UserRefreshTokenResDTO refreshResDTO = userAuthService.refreshAccessToken(refreshToken);
        writeRefreshTokenCookie(response, refreshToken, 7 * 24 * 60 * 60);
        return refreshResDTO;
    }

    @GetMapping("/captcha")
    @Operation(summary = "账号密码登录获取验证码")
    public void captcha() {
        userAuthService.captcha();
    }

    @GetMapping("/captcha/phone")
    @Operation(summary = "手机验证码登录获取验证码")
    public void captchaPhone(@RequestParam @NotBlank(message = "手机号不能为空")
                             @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        userAuthService.captchaPhone(phone);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "重置密码")
    public void resetPassword(@Valid @RequestBody UserResetPasswordReqDTO reqDTO) {
        userAuthService.userResetPassword(reqDTO);
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public UserRegisterResDTO register(@Valid @RequestBody UserRegisterReqDTO reqDTO) {
        return userAuthService.userRegister(reqDTO);
    }

    @PostMapping("/phone/change")
    @Operation(summary = "更换手机号")
    public void changePhone(@Valid @RequestBody UserPhoneChangeReqDTO userPhoneChangeReqDTO) {
        userAuthService.userChangePhone(userPhoneChangeReqDTO);
    }

    @PostMapping("/email/change/code")
    @Operation(summary = "获取更换邮箱验证码")
    public void emailChangeCode(@RequestParam @NotBlank(message = "邮箱不能为空")
                                @Email(message = "邮箱格式不正确") String email) {
        userAuthService.emailChangeCode(email);
    }

    @PostMapping("/email/change")
    @Operation(summary = "更换邮箱")
    public void changeEmail(@Valid @RequestBody UserEmailChangeReqDTO userEmailChangeReqDTO) {
        userAuthService.userChangeEmail(userEmailChangeReqDTO);
    }

    private void writeRefreshTokenCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(authProperties.isRefreshCookieSecure())
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(authProperties.isRefreshCookieSecure())
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
