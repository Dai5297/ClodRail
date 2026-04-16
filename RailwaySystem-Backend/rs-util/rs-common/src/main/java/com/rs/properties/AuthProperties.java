package com.rs.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "rs.auth")
public class AuthProperties {

    @NotBlank(message = "rs.auth.jwt-key must not be blank")
    private String jwtKey;

    private boolean refreshCookieSecure = true;
}
