package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
     WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 允許所有路徑
                        .allowedOrigins("http://localhost:3000") // 允許的來源
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的請求方法
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}