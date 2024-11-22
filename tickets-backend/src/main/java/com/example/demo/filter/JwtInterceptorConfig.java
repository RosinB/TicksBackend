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
                .addPathPatterns("/user/all") // 攔截的路徑 全部會員
                .addPathPatterns("/user/userUpdate")//更新會員
                .addPathPatterns("/user/order")//使用者訂單
                .addPathPatterns("/sales/goticket/*")
                .addPathPatterns("/event/ticket")
                .addPathPatterns("/admin/*")
                .excludePathPatterns("/user/login"); // 排除不需要攔截的路徑
    }
}
