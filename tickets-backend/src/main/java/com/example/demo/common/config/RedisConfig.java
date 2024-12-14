package com.example.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置 ObjectMapper 並添加 JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 使用自定義 ObjectMapper 配置 GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        //使用 Jackson 序列化器，將 Java 對象轉換為 JSON 存儲到 Redis 中，並從 JSON 還原為對象
        
        
        
        // 使用 String 序列化鍵
        //配置序列化器可以確保數據存儲時的一致性和可讀性。

        template.setKeySerializer(new StringRedisSerializer());
        // 使用 JSON 序列化值
        template.setValueSerializer(serializer);
        // 配置 Hash 鍵的序列化器為 String
        template.setHashKeySerializer(new StringRedisSerializer());
        // 配置 Hash 值的序列化器為 JSON
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
    
    
    @Bean
    public RedisTemplate<String, String> setOperationsTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}