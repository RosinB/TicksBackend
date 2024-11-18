package com.example.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Jackson 序列化工具

    // 保存數據到 Redis
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 保存數據到 Redis，並設置過期時間
    public void saveWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 獲取數據（泛型支持）
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        // 如果直接是正確類型，返回
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        // 否則反序列化為指定類型
        return objectMapper.convertValue(value, clazz);
    }
    

    // 獲取泛型數據（解決 List<EventDto> 的問題）
    public <T> T get(String key, TypeReference<T> typeReference) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        // 將數據反序列化為泛型類型
        return objectMapper.convertValue(value, typeReference);
    }

    // 刪除數據
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 檢查鍵是否存在
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 設置過期時間
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}