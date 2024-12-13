package com.example.demo.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.adminPanel.service.common.TrafficRecordService;
import com.example.demo.adminPanel.service.ml.UserRequestAnalyzer;
import com.example.demo.adminPanel.service.traffic.TrafficDtoBuilder;
import com.example.demo.common.config.RabbitMQConfig;
import com.example.demo.common.websocket.RequestLogWebSocketHandler;
import com.example.demo.common.websocket.TrafficStatsService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestStatisticsFilter extends OncePerRequestFilter {
    private final TrafficDtoBuilder trafficDtoBuilder;
    private final RabbitTemplate rabbitTemplate;
    private final TrafficStatsService trafficStatsService;
    private final RequestLogWebSocketHandler requestLogWebSocketHandler;
    private final UserRequestAnalyzer userRequestAnalyzer;
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);

        try {
        	
            trafficStatsService.recordRequest();

            TrafficDto trafficData = trafficDtoBuilder.buildFromRequest(request, requestId);
            request.setAttribute("trafficData", trafficData);

            filterChain.doFilter(request, response);
            
            // 更新完成時間
            updateResponseInfo(trafficData, response, System.currentTimeMillis() - startTime);
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.TRAFFIC_ROUTING_KEY,
                trafficData
            );

        } catch (Exception e) {
            log.error("Traffic statistics error", e);
            filterChain.doFilter(request, response);
        }
    }
    
    
    private void updateResponseInfo(TrafficDto trafficData, HttpServletResponse response, long executionTime) {
        trafficData.setExecutionTime(executionTime);
        trafficData.setSuccess(response.getStatus() >= 200 && response.getStatus() < 300);
        if (!trafficData.isSuccess()) {
            trafficData.setErrorMessage("HTTP Status: " + response.getStatus());
        }
    }
}