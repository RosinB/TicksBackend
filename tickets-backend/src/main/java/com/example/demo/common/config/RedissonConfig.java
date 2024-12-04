package com.example.demo.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
     RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress("redis://localhost:6379") // Redis 地址
              .setConnectionPoolSize(10)          // 連接池
              .setConnectionMinimumIdleSize(2);   // 最小連接次數
        return Redisson.create(config);
    }
}