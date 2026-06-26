package com.nehirozsari.smartpantry.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    private Duration accessTokenExpiration = Duration.ofMinutes(15);

    private Duration refreshTokenExpiration = Duration.ofDays(7);
}
