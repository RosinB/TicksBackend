package com.example.demo.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketTicketDto {
    private Integer eventId;
    private String section;
    
    // 添加驗證方法
    public boolean isValid() {
        return eventId != null && section != null && !section.trim().isEmpty();
    }
}