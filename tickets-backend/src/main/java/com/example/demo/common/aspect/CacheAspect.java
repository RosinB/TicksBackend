package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.util.RedisService;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

@Aspect     
@Component  
@Slf4j
public class CacheAspect {
    
    @Autowired
    private RedisService redisService;  
    
    @Around("@annotation(com.example.demo.common.annotation.Cacheable)")
    public Object handleCache(ProceedingJoinPoint joinPoint) throws Throwable {
        // 獲取方法的簽名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 獲取方法上的CacheableUser註解
        Cacheable cacheable = signature.getMethod().getAnnotation(Cacheable.class);
        // 構建緩存key
        log.info("CacheAble:"+cacheable);
        String cacheKey = buildCacheKey(joinPoint, cacheable);
        log.info("CacheKey:"+cacheKey);
        // 使用泛型通配符
        TypeReference<Object> typeReference = new TypeReference<Object>() {
            @Override
            public Type getType() {
                return signature.getMethod().getGenericReturnType();
            }
        };
        
        Object cachedValue = redisService.get(cacheKey, typeReference);
        
        if(cachedValue != null) {
            return cachedValue; 
        }
        
        // 如果緩存中沒有數據,執行原方法
        Object result = joinPoint.proceed();
        
        // 如果設置了過期時間
        if(cacheable.expireTime() > 0) {
            // 將結果存入緩存,並設置過期時間
            redisService.saveWithExpire(cacheKey, 
                                      result, 
                                      cacheable.expireTime(), 
                                      cacheable.timeUnit());
        } else {
            // 將結果存入緩存,不設置過期時間
            redisService.save(cacheKey, result);
        }
        System.out.println("緩存多少"+result);
        // 返回方法執行結果
        return result;
    }

    // 構建緩存key的私有方法
    private String buildCacheKey(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
    	
    	
    	
    	
    	
    	
    	  String key = cacheable.key();
          if(key.isEmpty()) {
              // 如果沒有指定key模板，使用預設的格式
              String prefix = cacheable.prefix();
              Object[] args = joinPoint.getArgs();
              return prefix + ":" + String.join(":", Arrays.stream(args)
                      .map(String::valueOf)
                      .collect(Collectors.toList()));
          } else {
              // 使用指定的key模板
              Object[] args = joinPoint.getArgs();
              MessageFormat messageFormat = new MessageFormat(key);
              return messageFormat.format(args);
          }
    }
}