package com.example.demo.adminPanel.service.ticket;

import java.util.concurrent.TimeUnit;


import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;
import com.example.demo.adminPanel.repository.ticket.AdTicketJDBC;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor  // 使用 lombok 自動生成構造器
public class AdTicketServiceImpl implements AdTicketService{

	
	private final AdOrdersJDBC adOrdersJDBC;
	private final AdTicketJDBC adTicketJDBC;
	private final SalesRepositoryJdbc salesRepositoryJdbc;
	private final RedisService redisService;

	
	
	
	@Override
	@Transactional
	public void clearTicket(Integer eventId,String section) {

		
		Integer tickets=adOrdersJDBC.updateOrderByUpdateTime(eventId,section);
		adTicketJDBC.updateRemaining(eventId, tickets,section);
		
	}

	@Override
	public void blanceTicket(Integer eventId, String section) {

		String stockKey = String.format(CacheKeys.Sales.STOCK, eventId,section);
        Integer dbStock = salesRepositoryJdbc.findRemaingByEventIdAndSection(eventId, section);
        log.info("================整票成功========"+dbStock+"=====================");
        redisService.saveWithExpire(stockKey, dbStock,5,TimeUnit.SECONDS);		
	}
	
	
	
	
	
}
