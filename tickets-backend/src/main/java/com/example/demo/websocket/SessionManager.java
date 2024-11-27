package com.example.demo.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import com.example.demo.model.dto.event.WebSocketTicketDto;

import java.util.*;

@Component
public class SessionManager {
    // 將每個 WebSocketSession 映射到多個 WebSocketTicketDto
    private final Map<WebSocketSession, List<WebSocketTicketDto>> sessionEventMap = new HashMap<>();

    // 添加新的訂閱
    public synchronized void addSession(WebSocketSession session, WebSocketTicketDto eventSection) {
        sessionEventMap.computeIfAbsent(session, k -> new ArrayList<>()).add(eventSection);
    }

    // 移除會話
    public synchronized void removeSession(WebSocketSession session) {
        sessionEventMap.remove(session);
    }

    // 獲取會話的訂閱映射
    public synchronized Map<WebSocketSession, List<WebSocketTicketDto>> getSessionEventMap() {
        return new HashMap<>(sessionEventMap);
    }

    // 檢查是否為空
    public synchronized boolean isEmpty() {
        return sessionEventMap.isEmpty();
    }
}
