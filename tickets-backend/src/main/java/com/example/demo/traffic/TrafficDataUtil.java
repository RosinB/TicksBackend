package com.example.demo.traffic;

import com.example.demo.model.dto.traffic.TrafficDto;
import jakarta.servlet.http.HttpServletRequest;

public class TrafficDataUtil {

    public static TrafficDto createTrafficData(String requestId, String userName, String requestType, HttpServletRequest request) {
        TrafficDto trafficData = new TrafficDto();
        trafficData.setRequestId(requestId);
        trafficData.setUserName(userName);
        trafficData.setTimestamp(System.currentTimeMillis());
        trafficData.setRequestType(requestType);
        trafficData.setRegion("Asia/Taipei"); 
        trafficData.setIpAddress(getClientIp(request)); // 拿 IP
        trafficData.setSuccess(true); 
        return trafficData;
    }

    
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}