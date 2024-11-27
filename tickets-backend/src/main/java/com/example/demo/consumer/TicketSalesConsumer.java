package com.example.demo.consumer;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.controller.EventController;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.repository.order.OrderRepositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.repository.sales.SalesRespositoryJdbcImpl;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.RedisService;


@Component
public class TicketSalesConsumer {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private SalesService salesService;

 
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    OrderRepositoryJdbc orderRepositoryJdbc;

    @RabbitListener(queues = RabbitMQConfig.TICKET_QUEUE_NAME)
    public void handleMessage(PostTicketSalesDto tickets) {
    	  String requestId = tickets.getRequestId();
          String orderStatusKey = "order:" + requestId;
            
          try {
              // 檢查是否已處理
              if (redisService.exists(orderStatusKey)) {
                  return; // 已處理，直接返回
              }
              // 處理購票邏輯
              salesService.buyTicket(tickets);
              
              redisService.saveWithExpire(orderStatusKey, "COMPLETED", 10, TimeUnit.MINUTES);
          } catch (Exception e) {
              redisService.saveWithExpire(orderStatusKey, "FAILED", 10, TimeUnit.MINUTES);
              logger.warn("票務不足");
          }
    	
            }
        
    
}
