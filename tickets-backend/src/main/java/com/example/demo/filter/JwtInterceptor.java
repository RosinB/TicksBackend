package com.example.demo.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
	private final static Logger logger =LoggerFactory.getLogger(JwtInterceptor.class);
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	
    	System.out.println("攔截器執行成功，請求路徑: " + request.getRequestURI());
        String requestURI = request.getRequestURI();

        // 從 Header 中獲取 Token
        String token = request.getHeader("Authorization");
        // 放行登入請求
        if ("/login".equals(requestURI)) {
            return true;
        }
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
