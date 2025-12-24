package com.rs;

import com.rs.properties.ExcludeProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PropertiesTest {

    @Autowired
    private ExcludeProperties excludeProperties;

    @Test
    public void excludeProperties() {
        for (String path : excludeProperties.getPaths()) {
            System.out.println(path);
        }
    }
}
