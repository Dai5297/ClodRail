package com.dai;

import com.dai.util.EncoderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncoderTest {

    @Test
    public void test() {
        String rawPassword = "123456";
        String encodedPassword = EncoderUtil.encrypt(rawPassword);
        System.out.println(encodedPassword);
        System.out.println(EncoderUtil.matches(rawPassword, encodedPassword));
    }
}
