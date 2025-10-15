package com.rs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 */
public class EncoderUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private EncoderUtil() {
        // 工具类不允许实例化
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 加密密码
     */
    public static String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断字符串是否为BCrypt加密格式
     */
    public static boolean isBCryptEncoded(String password) {
        return password != null &&
                (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }
}
