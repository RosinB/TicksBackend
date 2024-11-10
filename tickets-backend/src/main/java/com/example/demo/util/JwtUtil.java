package com.example.demo.util;

import java.util.Date;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 密鑰
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1天

    // 生成 Token
    public static String generateToken(String username) {
        return Jwts.builder() //創建一個 JWT 的構造器，用來設置 Token 的內容
                .setSubject(username) // 設置主體（Subject），這裡是用戶名（username），表明這個 Token 是屬於哪個用戶的。
                .setIssuedAt(new Date()) //設置簽發時間（iat），表示 Token 的生成時間。
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//設置過期時間（exp），表示 Token 什麼時候失效
                .signWith(key) //用生成的密鑰對 Token 進行簽名
                .compact();
    }

    // 驗證並解析 Token
    public static String validateToken(String token) throws JwtException {
        return Jwts.parserBuilder() //創建一個解析器，用來解析和驗證 Token。
                .setSigningKey(key) //設置簽名密鑰，解析時會使用這個密鑰來驗證 Token 是否被篡改。
                .build()
                .parseClaimsJws(token) //如果 Token 無效（例如簽名錯誤、過期等），會拋出 JwtException
                .getBody() //提取 Token 的主體部分（Payload），其中包含設置的數據，例如用戶名、過期時間等
                .getSubject(); // 返回用戶名
    }
}
