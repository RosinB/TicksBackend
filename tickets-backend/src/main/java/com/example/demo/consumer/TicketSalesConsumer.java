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
    private RedissonClient redissonClient;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    OrderRepositoryJdbc orderRepositoryJdbc;

    @RabbitListener(queues = RabbitMQConfig.TICKET_QUEUE_NAME)
    public void handleMessage(PostTicketSalesDto tickets) {
        String requestId = tickets.getRequestId();
        String lockKey = "lock:buyTicket:" + tickets.getEventId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 嘗試獲取鎖
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                logger.info("獲得鎖，開始處理購票邏輯，RequestID: {}", requestId);

                try {
                    salesService.buyTicket(tickets);
                    
                    orderRepositoryJdbc.updateOrderStatus(requestId, "COMPLETED");
//                    logger.info("購票請求處理完成，RequestID: {}", requestId);
                    
                } catch (Exception e) {
                    logger.error("購票失敗，RequestID: {}，錯誤原因: {}", requestId, e.getMessage());


                    try {
                        redisService.saveWithExpire("order:" + requestId, "FAILED", 10, TimeUnit.MINUTES);
                    } catch (Exception redisException) {
                        logger.error("保存失敗狀態到 Redis 時出現異常，RequestID: {}", requestId, redisException);
                    }
                    
 
                    
                }
            } else {
                logger.warn("未獲得鎖，RequestID: {}", requestId);
            }
        } catch (InterruptedException e) {
            logger.error("獲取分布式鎖失敗，RequestID: {}", requestId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
