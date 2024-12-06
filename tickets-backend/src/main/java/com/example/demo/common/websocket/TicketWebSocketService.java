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
import java.util.List;
import java.util.Map;
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
        try {
            String payload = message.getPayload();
//            log.info("【WebSocket】收到消息: {}", payload);  // 顯示原始消息
            
            ObjectMapper objectMapper = new ObjectMapper();
            WebSocketTicketDto subscription = objectMapper.readValue(payload, WebSocketTicketDto.class);
            
            
       
            if (subscription.getEventId() != null && subscription.getSection() != null) {
            	
                sessionManager.addSession(session, subscription);  // 直接使用 subscription
                
//                log.info("【WebSocket】成功添加訂閱: sessionId={}, eventId={}, section={}", 
//                    session.getId(), subscription.getEventId(), subscription.getSection());
                // 立即發送一次當前狀態
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
//                log.info("【WebSocket】立即發送狀態更新: {}", status);
            } else {
                log.warn("無效的訂閱請求: {}", payload);
            }
        } catch (Exception e) {
            log.error("處理訂閱請求時發生錯誤: {}", e.getMessage());
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

        sessionMap.forEach((session, subscriptions) -> {
            if (!isValidSession(session)) {
                sessionManager.removeSession(session);
                return;
            }

            subscriptions.forEach(subscription -> 
                sendTicketUpdate(session, subscription));
        });
    }

    private void sendTicketUpdate(WebSocketSession session, WebSocketTicketDto subscription) {
        try {
            Integer remaining = salesRepositoryJdbc.findRemaingByEventIdAndSection(
                subscription.getEventId(),
                subscription.getSection()
            );

            TicketUpdate update = new TicketUpdate(
                subscription.getEventId(),
                subscription.getSection(),
                remaining
            );

            synchronized (lock) {
                if (isValidSession(session)) {
                    session.sendMessage(new TextMessage(update.toJson()));
                }
            }
        } catch (Exception e) {
            log.error("發送票務更新失敗: sessionId={}, error={}", 
                session.getId(), e.getMessage());
        }
    }

    private boolean isValidSession(WebSocketSession session) {
        return session != null && session.isOpen();
    }

  

    

   
}