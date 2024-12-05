package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
//        log.info("CacheAble:"+cacheable);
        String cacheKey = buildCacheKey(joinPoint, cacheable);
//        log.info("CacheKey:"+cacheKey);
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
        // 返回方法執行結果
        return result;
    }

    private String buildCacheKey(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        String keyExpression = cacheable.key();
        String prefixKey = cacheable.prefixKey();
        
        if (keyExpression == null || keyExpression.isEmpty()) {
            return prefixKey;
        }
        try {
            // 創建 SpEL 解析器
            ExpressionParser parser = new SpelExpressionParser();
            
            // 創建評估上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            // 獲取方法參數
            Object[] args = joinPoint.getArgs();
            
            // 直接設置參數索引（這是關鍵）
            for (int i = 0; i < args.length; i++) {
                context.setVariable("a" + i, args[i]);
            }
            
            // 解析鍵的主體部分
            Expression exp = parser.parseExpression(keyExpression);
            String keyBody = exp.getValue(context, String.class);
            
            // 組合最終的緩存鍵
            return prefixKey + keyBody;
            
        } catch (Exception e) {
            log.error("解析緩存key失敗: {} with error: {}", keyExpression, e.getMessage());
            throw new RuntimeException("解析緩存key失敗", e);
        }
    }

}