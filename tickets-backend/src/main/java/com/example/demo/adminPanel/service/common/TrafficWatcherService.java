package com.example.demo.adminPanel.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficWatcherService {
	  private final RedisService redisService;
	    
	    // 記錄用戶行為
	    public void recordUserBehavior(TrafficDto trafficData) {
	    	log.info("trafficData:",trafficData);
	        long currentSecond = trafficData.getTimestamp() / 1000;
	        
	        recordBasicTraffic(trafficData);
	        
	        storeTrafficRecord(trafficData);
	        
	        recordUserActivity(trafficData, currentSecond);
	        
	        if (isScalper(trafficData.getUserName())) {
	            recordSuspiciousActivity(trafficData);
	        }
	    }
	    
	    
	    private void storeTrafficRecord(TrafficDto trafficData) {
	        String recordKey = CacheKeys.util.TRAFFIC_RECORD;

	        Map<String, Object> record = new HashMap<>();
	        // 基本請求資訊
	        record.put("requestId", trafficData.getRequestId());
	        record.put("timestamp", trafficData.getTimestamp());
	        record.put("userName", trafficData.getUserName());
	        record.put("ip", trafficData.getIpAddress());
	        record.put("eventId", trafficData.getEventId());
	        record.put("requestType", trafficData.getRequestType());
	        
	        // 設備和請求相關
	        record.put("deviceType", trafficData.getDeviceType());
	        record.put("userAgent", trafficData.getUserAgent());
	        record.put("sessionId", trafficData.getSessionId());
	        record.put("requestMethod", trafficData.getRequestMethod());
	        record.put("requestUrl", trafficData.getRequestUrl());
	        record.put("referrer", trafficData.getReferrer());
	        record.put("executionTime", trafficData.getExecutionTime());  // 添加執行時間

	        // 風險控制相關
	        record.put("isProxy", trafficData.getIsProxy());
	        record.put("isRobot", trafficData.getIsRobot());
	        record.put("requestFrequency", trafficData.getRequestFrequency());
	        
	        // 結果相關
	        record.put("success", trafficData.isSuccess());
	        record.put("errorMessage", trafficData.getErrorMessage());
	        
	        // 票務相關（如果有的話）
	        if (trafficData.getEventId() != null) {
	            record.put("ticketType", trafficData.getTicketType());
	            record.put("ticketQuantity", trafficData.getTicketQuantity());
	            record.put("price", trafficData.getPrice());
	        }

	        try {
	            String recordJson = new ObjectMapper().writeValueAsString(record);
	            redisService.listLeftPush(recordKey, recordJson);
	            log.info("儲存的訊息:" + recordJson);
	        } catch (Exception e) {
	            log.error("Redis 存儲失敗: ", e);
	        }
	        
	        // 限制列表長度，只保留最近的記錄
	        redisService.listTrim(recordKey, 0, 9999);
	    }
	    
	    
	    // 記錄基本流量
	    private void recordBasicTraffic(TrafficDto trafficData) {
	    	//總流量
	        String secondKey = CacheKeys.util.TRAFFIC_EVENTID + trafficData.getEventId() ;
	        redisService.increment(secondKey, 1);
	        redisService.expire(secondKey, 5, TimeUnit.MINUTES);
	    }
	    
	    
	    // 記錄用戶活動
	    private void recordUserActivity(TrafficDto trafficData, long currentSecond) {
	        String userKey = CacheKeys.util.USERS_BEHAVIOR + trafficData.getUserName() + currentSecond;
	        Map<String, Object> behavior = createBehaviorMap(trafficData);
	        redisService.hashMultiSet(userKey, behavior);
	        redisService.expire(userKey, 30, TimeUnit.MINUTES);
	    }
	    
	    
	    // 創建行為映射
	    private Map<String, Object> createBehaviorMap(TrafficDto trafficData) {
	        Map<String, Object> behavior = new HashMap<>();
	        behavior.put("ip", trafficData.getIpAddress());
	        behavior.put("device", trafficData.getDeviceType());
	        behavior.put("ticketCount", trafficData.getTicketQuantity());
	        behavior.put("userAgent", trafficData.getUserAgent());
	        return behavior;
	    }

	    // 檢查是否是黃牛
	    public boolean isScalper(String userName) {
	        return checkMultipleIPs(userName) || checkHighFrequency(userName);
	    }
	    
	    
	    
	    // 檢查多IP
	    private boolean checkMultipleIPs(String userName) {
	        Set<String> userIps = redisService.sMembers(CacheKeys.util.USERS_IPS + userName);
	        return userIps.size() > 3;
	    }
	    
	    // 檢查高頻訪問
	    private boolean checkHighFrequency(String userName) {
	        Integer requestCount = redisService.get(CacheKeys.util.REQUEST_FREQUENCY + userName, Integer.class);
	        return requestCount != null && requestCount > 100;
	    }
	    
	    
	    
	    // 確認黃牛後記錄訊息:
	    private void recordSuspiciousActivity(TrafficDto trafficData) {
	        String suspiciousKey = CacheKeys.util.SUSPICIOUS_USERS + trafficData.getUserName();
	        Map<String, Object> suspiciousInfo = new HashMap<>();
	        suspiciousInfo.put("lastDetected", System.currentTimeMillis());
	        suspiciousInfo.put("ip", trafficData.getIpAddress());
	        suspiciousInfo.put("reason", getSuspiciousReason(trafficData));
	        
	        redisService.hashMultiSet(suspiciousKey, suspiciousInfo);
	        redisService.expire(suspiciousKey, 24, TimeUnit.HOURS);
	        
	        log.warn("檢測到可疑用戶行為: {}", trafficData.getUserName());
	    }
	    private String getSuspiciousReason(TrafficDto trafficData) {
	        List<String> reasons = new ArrayList<>();
	        
	        // 檢查IP
	        if (checkMultipleIPs(trafficData.getUserName())) {
	            reasons.add("多IP訪問");
	        }
	        
	        // 檢查頻率
	        if (checkHighFrequency(trafficData.getUserName())) {
	            reasons.add("訪問頻率異常");
	        }
	        
	        // 檢查代理
	        if (trafficData.getIsProxy()) {
	            reasons.add("使用代理IP");
	        }
	        
	        // 檢查機器人特徵
	        if (trafficData.getIsRobot()) {
	            reasons.add("機器人特徵");
	        }

	
	        return String.join("; ", reasons);
	    }
	
}
