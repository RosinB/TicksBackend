package com.example.demo.consumer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

@Service
public class TrafficConsumer {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final Map<Long, Long> localTrafficStats = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.TRAFFIC_QUEUE_NAME) 
    public void processTrafficData(TrafficDto trafficData) {
        try {
            // 1. 獲取當前時間戳的秒數
            long currentSecond = trafficData.getTimestamp() / 1000;

            String redisKey = "traffic:stats";
            String field = String.valueOf(currentSecond);

            redisTemplate.opsForHash().increment(redisKey, field, 1);

            redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
            
            localTrafficStats.merge(currentSecond, 1L, Long::sum);

            
            Object currentValue = redisTemplate.opsForHash().get(redisKey, field);
           System.out.println("Redis 中當前秒請求數量: " + currentValue);
            
            
////           System.out.println("----------本地流量統計：-------------");
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
//            localTrafficStats.forEach((second, count) -> {
//                String readableTime = formatter.format(Instant.ofEpochSecond(second));
//                System.out.println("秒: " + readableTime + "，請求數量: " + count);
//            });
////            System.out.println("---------------------------------");
////           System.out.println("成功處理流量數據：" + trafficData);
        } catch (Exception e) {
            System.err.println("處理流量數據失敗：" + e.getMessage());
        }
    }
}