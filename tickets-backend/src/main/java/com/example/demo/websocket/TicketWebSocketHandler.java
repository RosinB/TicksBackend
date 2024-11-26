package com.example.demo.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TicketWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, WebSocketTicketDto> sessionEventMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("新客戶端連接: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("收到消息: " + payload);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WebSocketTicketDto eventSection = objectMapper.readValue(payload, WebSocketTicketDto.class);

            if (eventSection.getEventId() != null && eventSection.getSection() != null) {
                sessionEventMap.put(session, eventSection);
                System.out.println("客戶端訂閱事件: " + eventSection);
            } else {
                System.err.println("接收到的數據不完整: " + eventSection);
            }
        } catch (Exception e) {
            System.err.println("處理 handleTextMessage 出錯: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessionEventMap.remove(session);
        System.out.println("客戶端斷開連接: " + session.getId());
    }

    public Map<WebSocketSession, WebSocketTicketDto> getSessionEventMap() {
        return sessionEventMap;
    }
}
