package com.example.demo.common.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
    	Properties properties = new Properties();

    	// 圖片尺寸
    	properties.setProperty("kaptcha.image.width", "250"); // 寬度調整為250px，更寬敞
    	properties.setProperty("kaptcha.image.height", "100"); // 高度調整為100px，字體更突出

    	// 驗證碼文字
    	properties.setProperty("kaptcha.textproducer.char.length", "5"); // 5個字元，易於識別
    	properties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"); // 避免難辨識字元（如O和0, I和1）
    	properties.setProperty("kaptcha.textproducer.font.color", "0,0,128"); // 深藍色
    	properties.setProperty("kaptcha.textproducer.font.size", "60"); // 字體大小設為60px，清晰易讀

    	// 背景漸變
    	properties.setProperty("kaptcha.background.clear.from", "245,245,245"); // 起始顏色為淺灰
    	properties.setProperty("kaptcha.background.clear.to", "255,255,255"); // 結束顏色為白色

    	// 干擾線
    	properties.setProperty("kaptcha.noise.color", "lightGray"); // 淺灰色干擾線，避免過於雜亂
    	properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise"); // 預設干擾線

    	// 圖片樣式
    	properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple"); // 水波紋效果，輕微變形但不影響識別

        Config config = new Config(properties);
        return config.getProducerImpl();
    }
}