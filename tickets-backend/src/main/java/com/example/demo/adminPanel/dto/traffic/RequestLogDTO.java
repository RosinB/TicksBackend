package com.example.demo.adminPanel.dto.traffic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestLogDTO {
    private String type;        // "requestLog"
    private String userName;
    private String method;      // GET, POST 等
    private String path;        // 請求路徑
    private String ip;
    private int status;         // HTTP 狀態碼
    private boolean success;
    private long timestamp;
}