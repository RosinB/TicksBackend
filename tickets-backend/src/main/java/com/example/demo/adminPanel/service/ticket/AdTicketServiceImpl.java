package com.example.demo.adminPanel.service.ticket;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;
import com.example.demo.adminPanel.repository.orders.AdOrdersJDBCImpl;
import com.example.demo.adminPanel.repository.ticket.AdTicketJDBC;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.util.RedisService;

import jakarta.transaction.Transactional;

@Service
public class AdTicketServiceImpl implements AdTicketService{

	private final static Logger logger= LoggerFactory.getLogger(AdTicketServiceImpl.class);
	
	@Autowired
	AdOrdersJDBC adOrdersJDBC;
	@Autowired
	AdTicketJDBC adTicketJDBC;
	
	@Autowired
	SalesRepositoryJdbc salesRepositoryJdbc;
	@Autowired
	RedisService redisService;
	
	@Override
	@Transactional
	public void clearTicket(Integer eventId,String section) {

		
		
		Integer tickets=adOrdersJDBC.updateOrderByUpdateTime(eventId,section);
		adTicketJDBC.updateRemaining(eventId, tickets,section);
		
	}

	@Override
	public void blanceTicket(Integer eventId, String section) {

		String stockKey = "event:" + eventId + ":section:" + section + ":stock";
        Integer dbStock = salesRepositoryJdbc.findRemaingByEventIdAndSection(eventId, section);
        logger.info("================整票成功========"+dbStock+"=====================");
        redisService.saveWithExpire(stockKey, dbStock,5,TimeUnit.SECONDS);		
	}
	
	

	
	
	
}
