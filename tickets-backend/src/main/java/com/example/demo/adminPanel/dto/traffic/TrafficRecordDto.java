package com.example.demo.adminPanel.dto.traffic;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TrafficRecordDto {
	private String requestId;

	private String userName;
	private long timestamp;
	private String requestType;
	private String region;
	private String ip; // 注意: 這裡用 ip 而不是 ipAddress
	private Boolean success;

// 設備資訊
	private String userAgent;
	private String deviceType;
	private String sessionId;

// 請求詳細資訊
	private String requestMethod;
	private String requestUrl;
	private String referrer;

// 票務相關
	private Integer eventId;
	private String ticketType;
	private Integer ticketQuantity;
	private Integer price;

// 風險控制
	private Boolean isRobot;
	private Boolean isProxy;
	private Integer requestFrequency; // 這個欄位之前缺少
	private String errorMessage;
    private Long executionTime;  // 添加執行時間欄位

}
//	 	private Long timestamp;            // 時間戳
//	    private String userName;           // 用戶名
//	    private String ip;                 // IP地址
//	    private Long eventId;              // 活動ID
//	    private String requestType;        // 請求類型
//	    private String deviceType;         // 設備類型
//	    private Boolean isProxy;           // 是否使用代理
//	    private Boolean isRobot;           // 是否是機器人
//}
