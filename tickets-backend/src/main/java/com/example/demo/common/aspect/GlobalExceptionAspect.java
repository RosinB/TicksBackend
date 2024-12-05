package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class GlobalExceptionAspect {

	
    
  
	  // 攔截 service 層下所有類的所有方法(包含子包)
	  @Around("execution(* com.example.demo.service..*.*(..)) || "
	           + "execution(* com.example.demo.adminPanel.service..*.*(..))")	   
	  public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
	       try {
	           // 執行原方法
	           return joinPoint.proceed();
	           
	       } catch (Exception e) {
	           // 獲取當前類名,例如:UserService 
	           String serviceName = joinPoint.getTarget().getClass().getSimpleName();
	           
	           // 獲取當前執行的方法名,例如:getUser
	           String methodName = joinPoint.getSignature().getName();
	           
	           // 記錄錯誤日誌
	           log.error("{}中的{}方法發生錯誤: {}", 
	               serviceName,  // 例如:UserService
	               methodName,   // 例如:getUser  
	               e.getMessage());  // 錯誤訊息
	           
	           // 拋出 RuntimeException,將錯誤訊息往上傳
	           throw new RuntimeException(e.getMessage());
	       }
	   }
}
