package com.example.demo.common.aspect;

import java.util.Random;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.annotation.GenerateCaptcha;
import com.example.demo.util.RedisService;

@Aspect
@Component
public class UserBusinessAspect {

	@Autowired
	RedisService redisService;
	
    // 儲存驗證碼的 ThreadLocal
    private static final ThreadLocal<String> captchaHolder = new ThreadLocal<>();

	@Around("@annotation(generateCaptcha)")
	public Object handleCaptcha(ProceedingJoinPoint joinPoint,GenerateCaptcha generateCaptcha) throws Throwable{
		
		try {// 生成驗證碼
				String code = new Random().ints(generateCaptcha.length(), 0, 10)
							                .mapToObj(String::valueOf)
							                .collect(Collectors.joining());
	            // 存入 ThreadLocal
		        captchaHolder.set(code);
		        return joinPoint.proceed();
	
		}finally {
            // 使用完清理
			captchaHolder.remove();
    }}
	
	
    // 提供獲取驗證碼的方法
	 public static String getCaptcha() {
	        return captchaHolder.get();
	    }
}
