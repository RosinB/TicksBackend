package com.example.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)  // 表示這個註解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 表示這個註解在運行時依然存在，可以透過反射讀取
public @interface Cacheable {
    String prefix() default "";    // 緩存key的前綴
    String key() default "";       // 緩存key的模板,例如 "userDto:{0}" 
    long expireTime() default 10;   // 過期時間
    TimeUnit timeUnit() default TimeUnit.MINUTES;   // 時間單位
	}