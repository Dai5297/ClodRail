package com.rs.config;

import com.rs.util.JWTUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthConfigurationTest {

    @AfterEach
    void tearDown() {
        // Keep tests isolated because JWTUtil stores the key statically.
        JWTUtil.initialize("test-reset-jwt-key-1234567890-abcdef");
    }

    @Test
    void shouldRejectBlankJwtKeyDuringInitialization() {
        assertThrows(IllegalArgumentException.class, () -> JWTUtil.initialize(" "));
    }

    @Test
    void shouldInitializeJwtUtilWhenJwtKeyConfigured() {
        JWTUtil.initialize("test-jwt-key-1234567890-abcdef123456");

        assertDoesNotThrow(() -> JWTUtil.createAccessToken(1L, 60_000L));
    }
}
