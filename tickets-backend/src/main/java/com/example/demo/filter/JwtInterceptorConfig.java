package com.example.demo.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public JwtInterceptorConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/user/all") // 攔截的路徑
                .addPathPatterns("/user/userUpdate")
                .addPathPatterns("/sales/goticket/*")
                .addPathPatterns("/admin/*")
                .excludePathPatterns("/login", "/public/**"); // 排除不需要攔截的路徑
    }
}
