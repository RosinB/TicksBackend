package com.example.demo.common.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import com.example.demo.adminPanel.dto.traffic.RequestLogDTO;
import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestLogWebSocketHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Request Log WebSocket connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("Request Log WebSocket closed: {}", session.getId());
    }

    // 發送新的請求記錄
    public void sendRequestLog(TrafficDto trafficData) {
        if (!hasActiveSessions()) {
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(trafficData);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("Failed to send request log to session {}", session.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to send request log", e);
        }
    }

    private boolean hasActiveSessions() {
        sessions.removeIf(session -> !session.isOpen());
        return !sessions.isEmpty();
    }
}