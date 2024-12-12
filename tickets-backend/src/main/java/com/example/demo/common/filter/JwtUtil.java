package com.example.demo.common.filter;

import java.util.Base64;
import java.util.Date;
import java.security.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token認證失敗 ", e);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Failed to get username from token: ", e);
            throw new RuntimeException("Failed to get username from token");
        }
    }
    
    public String getUserNameFromHeader(String header) {
        try {
            if (header.startsWith("Bearer ")) {
                String token = header.substring(7); // 去掉前綴
                return getUsernameFromToken(token); // 使用已存在的方法提取用戶名
            } else {
                throw new RuntimeException("Authorization 標頭格式不正確");
            }
        } catch (Exception e) {
            logger.error("從 Authorization 標頭提取用戶名失敗: ", e);
            throw new RuntimeException("無法從 Authorization 標頭提取用戶名");
        }
    }
    
    
    
    
    
}