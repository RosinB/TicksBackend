package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.annotation.CacheableUser;
import com.example.demo.util.RedisService;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

@Aspect     
@Component  
public class CacheAspect {
    
    @Autowired
    private RedisService redisService;  
    
    // @Around註解表示這是環繞通知,可以在目標方法執行前後都進行處理
    // @annotation(CacheableUser)表示切入點是所有帶有@CacheableUser註解的方法
    @Around("@annotation(CacheableUser)") 
    public Object handleCache(ProceedingJoinPoint joinPoint) throws Throwable {
        // 獲取方法的簽名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 獲取方法上的CacheableUser註解
        
        CacheableUser cacheableUser = signature.getMethod().getAnnotation(CacheableUser.class);
        
        // 構建緩存key
        String cacheKey = buildCacheKey(joinPoint, cacheableUser);
        
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
        if(cacheableUser.expireTime() > 0) {
            // 將結果存入緩存,並設置過期時間
            redisService.saveWithExpire(cacheKey, 
                                      result, 
                                      cacheableUser.expireTime(), 
                                      cacheableUser.timeUnit());
        } else {
            // 將結果存入緩存,不設置過期時間
            redisService.save(cacheKey, result);
        }
        
        // 返回方法執行結果
        return result;
    }

    // 構建緩存key的私有方法
    private String buildCacheKey(ProceedingJoinPoint joinPoint, CacheableUser cacheableUser) {
    	  String key = cacheableUser.key();
          if(key.isEmpty()) {
              // 如果沒有指定key模板，使用預設的格式
              String prefix = cacheableUser.prefix();
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