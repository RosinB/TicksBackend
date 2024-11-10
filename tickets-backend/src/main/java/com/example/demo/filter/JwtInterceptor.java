package com.example.demo.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.util.JwtUtil;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 從 Header 中獲取 Token
        String token = request.getHeader("Authorization");
        System.out.println("這是token的標頭"+token);
        // 檢查 Token 是否存在
        if (token == null || !token.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false; // 中斷請求
        }

        // 驗證 Token 是否有效
        token = token.substring(7); // 去掉 "Bearer " 前綴
        if (!JwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return false; // 中斷請求
        }

        // Token 有效，繼續執行
        return true;
    }

    
}
