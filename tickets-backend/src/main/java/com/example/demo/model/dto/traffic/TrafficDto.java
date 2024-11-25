package com.example.demo.model.dto.traffic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficDto {
	private String requestId;   // 請求的唯一標識
    private String userName;      // 用戶標識
    private long timestamp;     // 請求的時間戳

    private String requestType; // 請求類型（可選）
    private String region;      // 地區（可選）
    private String ipAddress;   // IP 地址（可選）
    private boolean success;    // 是否成功（可選）
    
}
