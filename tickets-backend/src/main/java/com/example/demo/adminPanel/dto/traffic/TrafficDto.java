package com.example.demo.adminPanel.dto.traffic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrafficDto {
    // 基本請求資訊
    private String requestId;        // 請求的唯一標識
    private String userName;         // 用戶標識
    private long timestamp;          // 請求的時間戳
    private String requestType;      // 請求類型
    private String region;           // 地區
    private String ipAddress;        // IP 地址
    private boolean success;         // 是否成功
    // 設備資訊
    private String userAgent;        // 用戶瀏覽器和設備資訊
    private String deviceType;       // 設備類型（手機/電腦）
    private String sessionId;        // 會話ID
    
    // 請求詳細資訊
    private String requestMethod;    // HTTP 方法（GET/POST）
    private String requestUrl;       // 請求的URL
    private String referrer;         // 來源頁面
    
    // 票務相關
    private Integer eventId;            // 活動ID
    private String  ticketType;       // 票種
    private Integer ticketQuantity;  // 購票數量
    private Integer price;  // 交易金額
    
    // 風險控制
    private Boolean isRobot;         // 是否為機器人
    private Boolean isProxy;         // 是否使用代理
    private Integer requestFrequency;// 單位時間內的請求次數
    private String errorMessage;     // 如果失敗，錯誤訊息
    private Long executionTime;  // 添加執行時間欄位

}












