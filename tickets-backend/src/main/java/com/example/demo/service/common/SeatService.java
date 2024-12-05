package com.example.demo.service.common;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.util.RedisService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SeatService {
	@Autowired
	SalesRepositoryJdbc salesRepositoryJdbc;
	@Autowired 
	RedisService redisService;
	
	 public void validateSeats(Integer[] seats, String section, Integer eventId) {
	        for (Integer seat : seats) {
	            boolean isAvailable = salesRepositoryJdbc.existsSeatsByPoolNumber(seat, section, eventId);
	            if (!isAvailable) {
	                log.error("座位 {} 已被購買或不存在", seat);
	                throw new RuntimeException("選擇的座位已有人坐或不存在");
	            }
	        }
	    }
	 
	 public void processSeats(Integer[] seats, Integer userId, String section, 
             Integer eventId, Integer orderId, String requestId, String userName) {
		 
		 for (Integer seat : seats) {
				log.info("座位號: {}", seat);
				try {
					salesRepositoryJdbc.updateTicketOrderSeat(userId, section, eventId, seat, orderId);
					redisService.saveWithExpire("order:" + requestId, userName, 10, TimeUnit.MINUTES);
				} catch (Exception e) {
					log.error("訂單新增失敗");
					throw new RuntimeException("訂單新增失敗");

				}

			}
		 
		 
		 
	 }
}
