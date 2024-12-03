package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.demo.websocket.TicketWebSocketHandler;


@Configuration
@EnableWebSocket // 啟用 WebSocket 支持
public class WebSocketConfig implements WebSocketConfigurer {

    private final TicketWebSocketHandler ticketWebSocketHandler;

    public WebSocketConfig(TicketWebSocketHandler ticketWebSocketHandler) {
        this.ticketWebSocketHandler = ticketWebSocketHandler;
        System.out.println("WebSocketConfig 使用的 handler: " + ticketWebSocketHandler.hashCode());
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ticketWebSocketHandler, "/ws/tickets").setAllowedOrigins("*");
    }
}