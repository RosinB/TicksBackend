package com.example.demo.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.common.config.RabbitMQConfig;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestStatisticsFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;
    private static final String UNKNOWN = "unknown";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);

        try {
            // 在處理請求前收集資料
            TrafficDto trafficData = buildTrafficData(request, requestId, startTime);
            
            filterChain.doFilter(request, response);
            // 計算執行時間
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            // 請求完成後更新狀態
            trafficData.setSuccess(response.getStatus() >= 200 && response.getStatus() < 300);
            if (!trafficData.isSuccess()) {
                trafficData.setErrorMessage("HTTP Status: " + response.getStatus());
            }
            trafficData.setExecutionTime(executionTime);  // 設置執行時間（毫秒）

            // 發送到 RabbitMQ
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME, 
                RabbitMQConfig.TRAFFIC_ROUTING_KEY,
                trafficData
            );
            
        } catch (Exception e) {
            log.error("Error in RequestStatisticsFilter", e);
            filterChain.doFilter(request, response);
        }
    }
    
    private TrafficDto buildTrafficData(HttpServletRequest request, String requestId, long timestamp) {
        return TrafficDto.builder()
            .requestId(requestId)
            .timestamp(timestamp)
            .userName(getUserName(request))  // 傳入 request 參數
            .requestType("WEB")      // 可以根據需求設置
            .ipAddress(getClientIp(request))
            .userAgent(request.getHeader("User-Agent"))
            .deviceType(determineDeviceType(request.getHeader("User-Agent")))
            .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
            .requestMethod(request.getMethod())
            .requestUrl(request.getRequestURI())
            .referrer(request.getHeader("Referer"))
            .build();
    }
    
    private String getUserName(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            try {
                return jwtUtil.getUserNameFromHeader(token);
            } catch (Exception e) {
                log.debug("Token parsing failed: {}", e.getMessage());
            }
        }
        return UNKNOWN;
    }

    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多個代理，取第一個 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
    
    private String determineDeviceType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "MOBILE";
        }
        return "DESKTOP";
    }
}