package com.example.demo.adminPanel.service.traffic;

import org.springframework.stereotype.Component;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.common.filter.JwtUtil;
import com.example.demo.model.dto.sales.PostTicketSalesDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrafficDtoBuilder {
    private final JwtUtil jwtUtil;
    
    
    
    
    
    
    
    public TrafficDto buildFromRequest(HttpServletRequest request, String requestId) {
        return TrafficDto.builder()
            .requestId(requestId)
            .timestamp(System.currentTimeMillis())
            .userName(getUserName(request))
            .ipAddress(getClientIp(request))
            .requestType("WEB")
            .deviceType(detectDeviceType(request.getHeader("User-Agent")))
            .userAgent(request.getHeader("User-Agent"))
            .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
            .requestMethod(request.getMethod())
            .requestUrl(request.getRequestURI())
            .referrer(request.getHeader("Referer"))
            .isProxy(checkIfProxy(request))
            .isRobot(checkIfRobot(request))
            .build();
    }
    
    
    
    
    
    
    public TrafficDto buildFromTicketSales(PostTicketSalesDto salesDto, HttpServletRequest request, String requestId) {
        TrafficDto baseDto = buildFromRequest(request, requestId);
        return TrafficDto.builder()
            .requestId(baseDto.getRequestId())
            .timestamp(baseDto.getTimestamp())
            .userName(salesDto.getUserName())
            .ipAddress(baseDto.getIpAddress())
            .requestType("BUY_TICKET")
            .deviceType(baseDto.getDeviceType())
            .userAgent(baseDto.getUserAgent())
            .sessionId(baseDto.getSessionId())
            .requestMethod(baseDto.getRequestMethod())
            .requestUrl(baseDto.getRequestUrl())
            .referrer(baseDto.getReferrer())
            .isProxy(baseDto.isProxy())
            .isRobot(baseDto.isRobot())
            .eventId(salesDto.getEventId())
            .ticketType(salesDto.getSection())
            .ticketQuantity(salesDto.getQuantity())
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
        return "UNKNOWN";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";
        userAgent = userAgent.toLowerCase();
        return userAgent.contains("mobile") || 
               userAgent.contains("android") || 
               userAgent.contains("iphone") ? "MOBILE" : "DESKTOP";
    }

    private boolean checkIfProxy(HttpServletRequest request) {
        return request.getHeader("Via") != null || 
               request.getHeader("X-Forwarded-For") != null || 
               request.getHeader("Proxy-Client-IP") != null;
    }

    private boolean checkIfRobot(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return true;
        userAgent = userAgent.toLowerCase();
        return userAgent.contains("bot") || 
               userAgent.contains("crawler") || 
               userAgent.contains("spider");
    }
}