package com.example.demo.adminPanel.service.ml;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRequestAnalyzer {
    private final RedisService redisService;
    private static final String REQUEST_COUNT_PREFIX = "request:count:";
    private static final int THRESHOLD = 10; // 每秒請求閾值

    public boolean checkFrequency(String userName) {
        String currentSecond = String.valueOf(System.currentTimeMillis() / 1000/10);
        String key = REQUEST_COUNT_PREFIX + userName + ":" + currentSecond;
        
        Long count = redisService.increment(key, 1);
        redisService.expire(key, 11, TimeUnit.SECONDS);

        boolean isAnomalous = count > THRESHOLD;
        if (isAnomalous) {
            log.warn("使用者:{}頻率請求頻率過載:{}次數", userName, count);
        }
        
        return isAnomalous;
    }
}