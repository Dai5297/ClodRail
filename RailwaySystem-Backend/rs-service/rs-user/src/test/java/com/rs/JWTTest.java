package com.rs;

import com.rs.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTTest {

    @Test
    public void jwtTest() {
//        System.out.println(jwtProperties.getKey());
        String jwt = JWTUtil.createJWT("dai");
        System.out.println(jwt);
    }
}
