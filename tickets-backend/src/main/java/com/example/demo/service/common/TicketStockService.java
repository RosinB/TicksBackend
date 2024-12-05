package com.example.demo.service.common;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketStockService {
	
	private final RedisService redisService;
	private final SalesRepositoryJdbc salesRepositoryJdbc;
	
	
	 public Integer ensureStockInRedis( Integer eventId, String section) {
		 	String stockKey = String.format(CacheKeys.Sales.STOCK, eventId,section);

		 
	        Integer remainingStock = redisService.get(stockKey, Integer.class);
	        
	        if (remainingStock == null) {
	            remainingStock = salesRepositoryJdbc.findRemaingByEventIdAndSection(eventId, section);
	            if (remainingStock == null || remainingStock <= 0) {
	                throw new RuntimeException("該區域的庫存不存在或不足！");
	            }
	            redisService.saveWithExpire(stockKey, remainingStock, 5, TimeUnit.SECONDS);
	            log.info("Redis 無庫存記錄，從資料庫初始化，庫存: {}", remainingStock);
	        }
	        
	        return remainingStock;
	    }	
	 
	public Long decrementTicketStock(Integer eventId, String section ,Integer quantity){
		
	 	String stockKey = String.format(CacheKeys.Sales.STOCK, eventId,section);
	 	Long updatedStock = redisService.decrement(stockKey, quantity);
        log.info("目前剩餘票數: {}", updatedStock);
        
        if (updatedStock < 0) {
            throw new RuntimeException("庫存不足，購票失敗！");
        }
        return updatedStock;		
		
		
	}
	public void rollbackTicketStock(Integer eventId, String section, Integer quantity) {
	 	String stockKey = String.format(CacheKeys.Sales.STOCK, eventId,section);
        redisService.increment(stockKey, quantity);
        log.info("庫存回滾完成，回滾數量: {}", quantity);
    }
	 
	 
	 
}
