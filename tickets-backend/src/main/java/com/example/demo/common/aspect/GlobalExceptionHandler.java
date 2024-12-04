package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalExceptionHandler {

	
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
  
    @Around("execution(* com.example.demo.service.user.UserService.*.*(..))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
        	  log.error("UserService error in method {}: {}", 
                      joinPoint.getSignature().getName(), 
                      e.getMessage());            
       
              throw new RuntimeException(e.getMessage());

        }
    }
}
