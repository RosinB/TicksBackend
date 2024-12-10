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
	 	private Long timestamp;            // 時間戳
	    private String userName;           // 用戶名
	    private String ip;                 // IP地址
	    private Long eventId;              // 活動ID
	    private String requestType;        // 請求類型
	    private String deviceType;         // 設備類型
	    private Boolean isProxy;           // 是否使用代理
	    private Boolean isRobot;           // 是否是機器人
}
