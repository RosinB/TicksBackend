package com.example.demo.common.websocket;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SessionManager {
    private final Map<WebSocketSession, List<WebSocketTicketDto>> sessionEventMap = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session, WebSocketTicketDto eventSection) {
        // 使用 CopyOnWriteArrayList 而不是 ArrayList
        sessionEventMap.computeIfAbsent(session, k -> new CopyOnWriteArrayList<>())
                      .add(eventSection);
    }

    public void removeSession(WebSocketSession session) {
        sessionEventMap.remove(session);
    }

    public Map<WebSocketSession, List<WebSocketTicketDto>> getSessionEventMap() {
        // ConcurrentHashMap 已經是線程安全的，不需要創建新的 Map
        return sessionEventMap;
    }

    public boolean isEmpty() {
        return sessionEventMap.isEmpty();
    }
}  // 這裡缺少了這個閉合大括號                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    