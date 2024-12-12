package com.example.demo.common.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final List<String> publicUrls;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Value("${jwt.public-urls}") List<String> publicUrls) {
        this.jwtUtil = jwtUtil;
        this.publicUrls = publicUrls;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        if (isPublicUrl(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestURI.startsWith("/ws/")) {  // 跳過 WebSocket 路徑
        	filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            sendErrorResponse(response, "Missing or invalid token");
            return;
        }

        token = token.substring(7);

        try {
            if (jwtUtil.isTokenValid(token)) {
                // 設置 Spring Security 的認證信息
                String username = jwtUtil.getUsernameFromToken(token);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                sendErrorResponse(response, "Token is invalid or expired");
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Token validation failed");
        }
    }

    private boolean isPublicUrl(String requestURI) {
        return publicUrls.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("timestamp", new Date().toString());
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}