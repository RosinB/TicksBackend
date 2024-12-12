package com.example.demo.common.config;

import com.example.demo.common.websocket.OrderStatusWebSocketHandler;
import com.example.demo.common.websocket.TicketWebSocketService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final TicketWebSocketService ticketWebSocketService;
    private final OrderStatusWebSocketHandler orderStatusWebSocketHandler;
   

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	 registry.addHandler(ticketWebSocketService, "/ws/tickets")
         .setAllowedOrigins("*");

    	 registry.addHandler(orderStatusWebSocketHandler, "/ws/order/status")
         .setAllowedOrigins("*");
    }

}