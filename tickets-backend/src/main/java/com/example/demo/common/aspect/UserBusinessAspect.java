package com.example.demo.common.aspect;

import java.util.Random;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.util.RedisService;

@Aspect
@Component
public class UserBusinessAspect {

	@Autowired
	RedisService redisService;
	
   
}
