package com.example.demo.common.websocket;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import com.example.demo.model.dto.ticket.TicketUpdate;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketWebSocketService extends TextWebSocketHandler {
    private final SessionManager sessionManager;
    private final SalesRepositoryJdbc salesRepositoryJdbc;
    private final ThreadPoolTaskScheduler taskScheduler;  // 改用 ThreadPoolTaskScheduler
    private volatile ScheduledFuture<?> scheduledFuture;
    private final Object lock = new Object();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("新的 WebSocket 連接建立: {}", session.getId());
        startQueryServiceIfNeeded();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        synchronized (session) {
            try {
                String payload = message.getPayload();
                ObjectMapper objectMapper = new ObjectMapper();
                WebSocketTicketDto subscription = objectMapper.readValue(payload, WebSocketTicketDto.class);
                
                if (subscription.getEventId() != null && subscription.getSection() != null) {
                    sessionManager.addSession(session, subscription);
                    
                    Integer remaining = salesRepositoryJdbc.findRemaingByEventIdAndSection(
                        subscription.getEventId(),
                        subscription.getSection()
                    );
                    String status = String.format(
                        "{\"eventId\": %d, \"section\": \"%s\", \"remainingTickets\": %d}",
                        subscription.getEventId(),
                        subscription.getSection(),
                        remaining
                    );
                    session.sendMessage(new TextMessage(status));
                } else {
                    log.warn("無效的訂閱請求: {}", payload);
                }
            } catch (Exception e) {
                log.error("處理訂閱請求時發生錯誤: {}", e.getMessage());
                sessionManager.removeSession(session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        synchronized (lock) {
            sessionManager.removeSession(session);
            log.info("WebSocket 連接關閉: {}, 狀態: {}", session.getId(), status);
            stopQueryServiceIfNoSubscribers();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 傳輸錯誤: {}, 錯誤: {}", session.getId(), exception.getMessage());
        sessionManager.removeSession(session);
        stopQueryServiceIfNoSubscribers();
    }

    private void startQueryServiceIfNeeded() {
        synchronized (lock) {
            if (scheduledFuture == null || scheduledFuture.isCancelled()) {
                scheduledFuture = taskScheduler.scheduleAtFixedRate(
                    this::updateTicketStatus,
                    Instant.now(),
                    Duration.ofSeconds(1)
                );
                log.info("票務查詢服務已啟動");
            }
        }
    }

    private void stopQueryServiceIfNoSubscribers() {
        synchronized (lock) {
            if (sessionManager.isEmpty() && scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                log.info("票務查詢服務已停止");
            }
        }
    }

    private void updateTicketStatus() {
        Map<WebSocketSession, List<WebSocketTicketDto>> sessionMap = sessionManager.getSessionEventMap();
        if (sessionMap.isEmpty()) {
            return;
        }

        Set<WebSocketSession> invalidSessions = ConcurrentHashMap.newKeySet();

        for (Map.Entry<WebSocketSession, List<WebSocketTicketDto>> entry : sessionMap.entrySet()) {
            WebSocketSession session = entry.getKey();
            List<WebSocketTicketDto> subscriptions = entry.getValue();

            if (!isValidSession(session)) {
                invalidSessions.add(session);
                continue;
            }

            for (WebSocketTicketDto subscription : subscriptions) {
                sendTicketUpdate(session, subscription);
            }
        }

        // 移除無效的 session
        invalidSessions.forEach(session -> {
            sessionManager.removeSession(session);
        });
    }
    
    

    private void sendTicketUpdate(WebSocketSession session, WebSocketTicketDto subscription) {
        // 添加同步機制來確保消息按順序發送
        synchronized (session) {
            try {
                if (!isValidSession(session)) {
                    return;
                }

                Integer remaining = salesRepositoryJdbc.findRemaingByEventIdAndSection(
                    subscription.getEventId(),
                    subscription.getSection()
                );

                TicketUpdate update = new TicketUpdate(
                    subscription.getEventId(),
                    subscription.getSection(),
                    remaining
                );

                session.sendMessage(new TextMessage(update.toJson()));
            } catch (Exception e) {
                log.error("發送票務更新失敗: sessionId={}, error={}", 
                    session.getId(), e.getMessage());
                // 如果發送失敗，標記 session 為無效
                sessionManager.removeSession(session);
            }
        }
    }

    private boolean isValidSession(WebSocketSession session) {
        return session != null && session.isOpen();
    }

  

    

   
}