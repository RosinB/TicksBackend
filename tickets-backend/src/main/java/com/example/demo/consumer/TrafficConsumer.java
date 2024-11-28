package com.example.demo.consumer;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.controller.EventController;
import com.example.demo.model.dto.traffic.TrafficDto;
import com.example.demo.util.RedisService;

@Service
public class TrafficConsumer {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final Map<Long, Long> localTrafficStats = new ConcurrentHashMap<>();
    @Autowired
    RedisService redisService;
    
    @RabbitListener(queues = RabbitMQConfig.TRAFFIC_QUEUE_NAME)
    public void processTrafficData(TrafficDto trafficData) {
        try {
            long currentSecond = trafficData.getTimestamp() / 1000;

            String redisKey = "traffic:stats";
            String field = currentSecond + "_" + trafficData.getRequestType() + "_" + trafficData.getRegion();
            String totalTrafficKey = "traffic:stats:" + currentSecond;
            // 累計總請求數
            redisService.increment(totalTrafficKey, 1);
            redisService.expire(totalTrafficKey, 5, TimeUnit.MINUTES);
            
            
            
            redisService.increment(redisKey + ":" + field, 1);

            redisService.expire(redisKey + ":" + field, 5, TimeUnit.MINUTES); // 為具體鍵設置過期

            Long currentValue = redisService.get(redisKey + ":" + field, Long.class);
            System.out.println("當前秒請求數量: " + currentValue);

        } catch (Exception e) {
            System.err.println("處理流量數據失敗：" + e.getMessage());
        }
    }
}