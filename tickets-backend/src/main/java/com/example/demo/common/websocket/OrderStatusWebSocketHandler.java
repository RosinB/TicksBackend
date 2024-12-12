package com.example.demo.common.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.service.order.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusWebSocketHandler extends TextWebSocketHandler {
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // 前端的requestId
            JsonNode node = objectMapper.readTree(message.getPayload());
            String requestId = node.get("requestId").asText();
            
            // 開始定期檢查訂單狀態
            
            checkOrderStatus(session, requestId);
        } catch (Exception e) {
            log.error("處理 WebSocket 消息時發生錯誤", e);
            sendErrorMessage(session, "處理請求時發生錯誤");
        }
    }

    private void checkOrderStatus(WebSocketSession session, String requestId) {
        if (!session.isOpen()) {
            return;
        }

        try {
            Map<String, Object> status = orderService.getTicketStatus(requestId);
            // 將狀態轉為 JSON 並發送給前端
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(status)));


            // 如果不是最終狀態，繼續檢查
            String orderStatus = (String) status.get("status");
            if ("付款中".equals(orderStatus) || "錯誤".equals(orderStatus)) {
                session.close(CloseStatus.NORMAL);
                return;
            }
            
            // 如果不是最終狀態，1秒後再次檢查
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
            .execute(() -> checkOrderStatus(session, requestId));
            
        } catch (Exception e) {
            log.error("檢查訂單狀態時發生錯誤", e);
            sendErrorMessage(session, "檢查訂單狀態時發生錯誤");
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("關閉 WebSocket 連接時發生錯誤", ex);
            }
    }}

    
    private void sendErrorMessage(WebSocketSession session, String message) {
        try {
            Map<String, String> errorResponse = Map.of(
                "status", "錯誤",
                "message", message
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
        } catch (Exception e) {
            log.error("發送錯誤消息失敗", e);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        log.info("WebSocket 連接關閉: {}, 狀態: {}", session.getId(), status);
          log.info("WebSocket 連接關閉。");
    	// 這裡可以添加清理資源的邏輯
    }

    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 傳輸錯誤: {}, 錯誤: {}", session.getId(), exception.getMessage());
        try {
            session.close();
        } catch (IOException e) {
            log.error("關閉 WebSocket 連接時發生錯誤", e);
        }
    }
}

