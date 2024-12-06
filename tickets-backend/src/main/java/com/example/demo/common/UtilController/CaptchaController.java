package com.example.demo.common.UtilController;

import com.example.demo.util.ApiResponse;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CaptchaController {

    private final Producer kaptchaProducer;
    private final RedisService redisService;
    
    
   
    
    
    @GetMapping("/captcha/{userName}")
    public ResponseEntity<ApiResponse<Object >> getCaptcha(@PathVariable("userName") String  userName){
    	  try {
    	        // 生成驗證碼文本
    	        String captchaText = kaptchaProducer.createText();
    	        log.info("產生的驗證碼:"+captchaText);
    	        
    	        redisService.saveWithExpire(CacheKeys.util.CAPTCHA_PREFIX+userName,captchaText , 1, TimeUnit.MINUTES);
    	        
    	        // 生成驗證碼圖片
    	        BufferedImage image = kaptchaProducer.createImage(captchaText);

    	        // 將圖片轉換為 Base64 格式
    	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	        ImageIO.write(image, "png", baos);
    	        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

    	        // 返回 Base64 圖片和驗證碼的 Key（此處假設直接返回文本）
    	        Map<String, String> data = new HashMap<>();
    	        data.put("captchaImage", "data:image/png;base64," + base64Image);
    	        data.put("captchaKey", captchaText); // 或將 captchaKey 存入 Redis

    	        return ResponseEntity.ok(ApiResponse.success("驗證碼生成成功", data));
    	    } catch (IOException e) {
    	        return ResponseEntity.status(400)
    	                .body(ApiResponse.error(400, "傳達失敗", null));
    	    }
    
    }
    
    
  
}
