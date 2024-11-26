package com.example.demo.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import com.example.demo.model.dto.event.WebSocketTicketDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<WebSocketSession, WebSocketTicketDto> sessionEventMap = new ConcurrentHashMap<>();

    public Map<WebSocketSession, WebSocketTicketDto> getSessionEventMap() {
        return sessionEventMap;
    }

    public void addSession(WebSocketSession session, WebSocketTicketDto eventSection) {
        sessionEventMap.put(session, eventSection);
    }

    public void removeSession(WebSocketSession session) {
        sessionEventMap.remove(session);
    }

    public boolean isEmpty() {
        return sessionEventMap.isEmpty();
    }
}
