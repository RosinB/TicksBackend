package com.example.demo.adminPanel.service.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrafficRecordService {

	private final RedisService redisService;
    private static final long MINUTES = 60; // 5分鐘 = 300秒

	public TrafficDto createTrafficDto(PostTicketSalesDto data,HttpServletRequest request,String requestId) {
		Integer frequency=getRequestFrequency(data.getUserName());

		
		
		return  TrafficDto.builder()
				.requestId(requestId)
	            .userName(data.getUserName())
	            .timestamp(System.currentTimeMillis())
	            .requestType("BUY_TICKET")
	            .region("Asia/Taipei")
	            .ipAddress(getClientIp(request))
	            .success(true)
	            .userAgent(request.getHeader("User-Agent"))
	            .deviceType(detectDeviceType(request.getHeader("User-Agent")))
	            .sessionId(request.getSession().getId())
	            
	            // 請求詳細資訊
	            .requestMethod(request.getMethod())
	            .requestUrl(request.getRequestURL().toString())
	            .referrer(request.getHeader("Referer"))
	            // 票務相關
	            .eventId(data.getEventId())
	            .ticketType(data.getSection())
	            .ticketQuantity(data.getQuantity())	            
	            // 風險控制
	            .isRobot(checkIfRobot(request))
	            .isProxy(checkIfProxy(request))
	            .requestFrequency(frequency)
	            .build();

	}
	
	 private Integer getRequestFrequency(String userName) {
	        String cacheKey = CacheKeys.util.REQUEST_FREQUENCY + userName;
	        
	        // 增加計數並設定過期時間
	        Long count = redisService.increment(cacheKey, 1);
	        if (count == 1) {
	            redisService.expire(cacheKey, MINUTES,TimeUnit.SECONDS);
	        }
	        
	        return count.intValue();
	    }
	 
	 public void recordUserBehavior(TrafficDto trafficData) {
	        String userName = trafficData.getUserName();
	        String userIpKey = "user:" + userName + ":ips";
	        
	        // 記錄用戶IP
	        redisService.sAdd(userIpKey, trafficData.getIpAddress());
	        redisService.expire(userIpKey, 3, TimeUnit.MINUTES);  // 3分鐘過期

	        // 記錄用戶行為
	        String userBehaviorKey = "user:" + userName + ":behavior";
	        Map<String, Object> behavior = new HashMap<>();
	        behavior.put("lastIp", trafficData.getIpAddress());
	        behavior.put("lastDevice", trafficData.getDeviceType());
	        behavior.put("lastAccess", trafficData.getTimestamp());
	        redisService.hashMultiSet(userBehaviorKey, behavior);
	    }
	 
	 
	   // 取得客戶端 IP
    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    
    // 檢測設備類型
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "mobile";
        }
        return "desktop";
    }
    
    // 檢查是否使用代理
    private boolean checkIfProxy(HttpServletRequest request) {
        return request.getHeader("Via") != null || 
               request.getHeader("X-Forwarded-For") != null || 
               request.getHeader("Proxy-Client-IP") != null;
    }
    
    // 檢查是否為機器人
    private boolean checkIfRobot(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return true;
        userAgent = userAgent.toLowerCase();
        return userAgent.contains("bot") || 
               userAgent.contains("crawler") || 
               userAgent.contains("spider");
    }
   
	
}
