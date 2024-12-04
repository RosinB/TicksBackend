package com.example.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 在運行時保留
public @interface GenerateCaptcha {
    int length() default 6;  // 可配置驗證碼長度，預設6位
}