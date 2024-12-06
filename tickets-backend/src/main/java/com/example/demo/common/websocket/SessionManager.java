package com.example.demo.common.websocket;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<WebSocketSession, List<WebSocketTicketDto>> sessionEventMap = new ConcurrentHashMap<>();

    public synchronized void addSession(WebSocketSession session, WebSocketTicketDto eventSection) {
        sessionEventMap.computeIfAbsent(session, k -> new ArrayList<>()).add(eventSection);
    }

    public synchronized void removeSession(WebSocketSession session) {
        sessionEventMap.remove(session);
    }

    public synchronized Map<WebSocketSession, List<WebSocketTicketDto>> getSessionEventMap() {
        return new HashMap<>(sessionEventMap);
    }

    public synchronized boolean isEmpty() {
        return sessionEventMap.isEmpty();
    }
}
