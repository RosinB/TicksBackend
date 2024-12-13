package com.example.demo.adminPanel.dto.traffic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrafficDto {
    // 基本請求信息
    private String requestId;
    private long timestamp;
    private String userName;
    private String ipAddress;
    private Integer eventId;
    private String requestType;
    
    // 設備和請求相關
    private String deviceType;
    private String userAgent;
    private String sessionId;
    private String requestMethod;
    private String requestUrl;
    private String referrer;
    private long executionTime;
    
    // 風險控制相關
    private boolean isProxy;
    private boolean isRobot;
    private int requestFrequency;
    
    // 結果相關
    private boolean success;
    private String errorMessage;
    
    // 票務相關
    private String ticketType;
    private int ticketQuantity;
    private int price;
    
}












