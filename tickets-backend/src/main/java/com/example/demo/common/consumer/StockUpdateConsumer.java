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
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.service.user.UserService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class StockUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final SalesRepositoryJdbc salesRepositoryJdbc;
    private final RedisService redisService;
    private final UserService userService;
  

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE_NAME)
    public void handleStockUpdate(PostTicketSalesDto tickets) {
        String requestId = tickets.getRequestId();
       
        try {
            // 更新資料庫庫存
            salesRepositoryJdbc.checkTicketAndUpdate(tickets.getSection(), tickets.getEventId(), tickets.getQuantity());

            // 獲取用戶 ID
            Integer userId =userService.getUserId(tickets.getUserName());

            
            // 創建訂單
            salesRepositoryJdbc.addTicketOrder(userId, tickets.getSection(), tickets.getEventId(), tickets.getQuantity(), requestId);
            redisService.saveWithExpire("order:" + requestId, tickets.getUserName(), 10, TimeUnit.MINUTES);
            logger.info("訂單創建成功: UserId={}, EventId={}, Section={}, Quantity={}, RequestId={}",
                    userId, tickets.getEventId(), tickets.getSection(), tickets.getQuantity(), requestId);        
            } catch (Exception e) {
            	logger.info("票卷更新失敗: "+e.getMessage());
            }
    }
    }
