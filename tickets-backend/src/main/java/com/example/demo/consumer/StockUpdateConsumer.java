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
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.util.RedisService;


@Component
public class StockUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private SalesRepositoryJdbc salesRepositoryJdbc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

  

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE_NAME)
    public void handleStockUpdate(PostTicketSalesDto tickets) {
        String requestId = tickets.getRequestId();

        try {
            // 更新資料庫庫存
            salesRepositoryJdbc.checkTicketAndUpdate(tickets.getSection(), tickets.getEventId(), tickets.getQuantity());

            // 獲取用戶 ID
            String cacheKey = "userId:" + tickets.getUserName();
            Integer userId = redisService.get(cacheKey, Integer.class);
            if (userId == null) {
                userId = userRepository.findIdByUserName(tickets.getUserName());
                redisService.saveWithExpire(cacheKey, userId, 10, TimeUnit.MINUTES);
            }

            
            // 創建訂單
            salesRepositoryJdbc.addTicketOrder(userId, tickets.getSection(), tickets.getEventId(), tickets.getQuantity(), requestId);
            logger.info("訂單創建成功: UserId={}, EventId={}, Section={}, Quantity={}, RequestId={}",
                    userId, tickets.getEventId(), tickets.getSection(), tickets.getQuantity(), requestId);        
            } catch (Exception e) {
            	logger.info("票卷更新失敗: "+e.getMessage());
            }
    }
    }
