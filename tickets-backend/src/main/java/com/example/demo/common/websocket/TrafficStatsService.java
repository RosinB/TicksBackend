package com.example.demo.common.websocket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficStatsDTO;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficStatsService {
    private final RedisService redisService;
    private final TrafficWebSocketHandler webSocketHandler;
    private static final String QPS_KEY = "traffic:qps";
    private static final String TOTAL_KEY_PREFIX = "total_traffic:";

    public void recordRequest() {
        try {
            // 記錄日總流量
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String totalKey = TOTAL_KEY_PREFIX + today;
            redisService.increment(totalKey, 1);
            
            // 記錄當前秒的請求數
            String currentSecond = String.valueOf(System.currentTimeMillis() / 1000);
            String qpsKey = QPS_KEY + ":" + currentSecond;
            redisService.increment(qpsKey, 1);
            redisService.expire(qpsKey, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to record request", e);
        }
    }

    
    @Scheduled(fixedRate = 1000)
    public void broadcastStats() {
    	if (!webSocketHandler.hasActiveSessions()) {
            return;  // 如果沒有活躍連接，直接返回
        }
        try {
        	  
            TrafficStatsDTO stats = TrafficStatsDTO.builder()
                .type("trafficStats")
                .totalTraffic(getTotalTrafficToday())
                .qps(calculateCurrentQPS())
                .timestamp(System.currentTimeMillis())
                .build();
                
            webSocketHandler.broadcastTrafficStats(stats);
        } catch (Exception e) {
            log.error("Failed to broadcast traffic stats", e);
        }
    }

    private long getTotalTrafficToday() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String key = TOTAL_KEY_PREFIX + today;
        Long total = redisService.get(key, Long.class);
        return total != null ? total : 0L;
    }

    private int calculateCurrentQPS() {
        String currentSecond = String.valueOf(System.currentTimeMillis() / 1000);
        String qpsKey = QPS_KEY + ":" + currentSecond;
        Integer qps = redisService.get(qpsKey, Integer.class);
        return qps != null ? qps : 0;
    }
}