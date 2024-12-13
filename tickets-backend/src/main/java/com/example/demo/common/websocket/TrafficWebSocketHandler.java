package com.example.demo.common.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.stereotype.Component;

import com.example.demo.adminPanel.dto.traffic.TrafficStatsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TrafficWebSocketHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("開啟WEBSOCKET: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("關閉WEBSOCKET: {}", session.getId());
    }
    

    public void broadcastTrafficStats(TrafficStatsDTO stats) {
        String message;
        try {
            message = objectMapper.writeValueAsString(stats);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast traffic stats", e);
        }
    }
    
    public boolean hasActiveSessions() {
        // 清理已關閉的會話
        sessions.removeIf(session -> !session.isOpen());
        return !sessions.isEmpty();
    }
    
}