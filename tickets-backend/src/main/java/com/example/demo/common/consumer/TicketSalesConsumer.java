package com.example.demo.common.consumer;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.config.RabbitMQConfig;
import com.example.demo.controller.EventController;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.repository.order.OrderRepositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.repository.sales.SalesRespositoryJdbcImpl;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class TicketSalesConsumer {

    private final  SalesService salesService;

    private final  RedisService redisService;
    

    @RabbitListener(queues = RabbitMQConfig.TICKET_QUEUE_NAME)
    public void handleMessage(PostTicketSalesDto tickets) {
    	  String requestId = tickets.getRequestId();
          String orderStatusKey = "order:" + requestId;
         
              // 檢查是否已處理
              if (redisService.exists(orderStatusKey)) {
                  return; // 已處理，直接返回
              }
              try {
                  salesService.buyTicket(tickets);
                  redisService.saveWithExpire(orderStatusKey, "付款中", 10, TimeUnit.MINUTES);

			} catch (Exception e) {
			}
              
    }}
          
          
          
    	
            
        
    

