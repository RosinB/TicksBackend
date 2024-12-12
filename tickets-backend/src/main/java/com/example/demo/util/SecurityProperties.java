package com.example.demo.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class SecurityProperties {
    private String secret;
    private long expiration;
    
    @Value("${jwt.public-urls}")
    private String publicUrlsString;
    
    public List<String> getPublicUrls() {
        return Arrays.asList(publicUrlsString.split(","));
    }
}