package com.example.demo.consumer;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.model.dto.traffic.TrafficDto;

@Service
public class TrafficConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(queues = RabbitMQConfig.TRAFFIC_QUEUE_NAME) // 監聽流量監控隊列
    public void processTrafficData(TrafficDto trafficData) {
        try {
            // 1. 獲取當前時間戳的秒數
            long currentSecond = trafficData.getTimestamp() / 1000;

            // 2. 使用 Redis 的 Hash 結構記錄每秒請求數量
            String redisKey = "traffic:stats";
            redisTemplate.opsForHash().increment(redisKey, String.valueOf(currentSecond), 1);

            // 3. 設置 Redis 過期時間，例如 5 分鐘
            redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);

            // 4. 記錄日誌
            System.out.println("成功處理流量數據：" + trafficData);
        } catch (Exception e) {
            System.err.println("處理流量數據失敗：" + e.getMessage());
        }
    }
}