package com.rs;

import com.rs.util.EncoderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;

@SpringBootTest
public class EncoderTest {

    @Test
    public void test() {
        String rawPassword = "123123";
        String encodedPassword = EncoderUtil.encrypt(rawPassword);
        System.out.println(encodedPassword);
        System.out.println(EncoderUtil.matches(rawPassword, encodedPassword));
    }

    public static void main(String[] args) throws Exception {
        String password = "123456";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        System.out.println(hex.toString());
    }
}
