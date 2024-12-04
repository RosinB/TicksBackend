package com.example.demo.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;

@Aspect
@Component
public class LoginAspect {
	private static final Logger logger = LoggerFactory.getLogger(LoginAspect.class);

	@Around("@annotation(com.example.demo.common.annotation.LoginCheck)")
	public Object handleLogin(ProceedingJoinPoint joinPoint) throws Throwable {
		LoginDto loginDto = (LoginDto) joinPoint.getArgs()[0];

		logger.info("用戶登入嘗試: {}", loginDto.getUserName());

		try {

			Object result = joinPoint.proceed();
			LoginResultDto loginResult = (LoginResultDto) result;
			if (loginResult.getSuccess()) {
				logger.info("用戶: {} 登入成功", loginDto.getUserName());
			} else {
				logger.warn("用戶: {} 登入失敗, 原因: {}", loginDto.getUserName(), loginResult.getMessage());
			}
			return result;

		} catch (Exception e) {
			logger.error("用戶登入發生異常: {}, 錯誤: {}", loginDto.getUserName(), e.getMessage());
			throw e;
		}

	}

}
