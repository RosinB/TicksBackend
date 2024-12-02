package com.example.demo.adminPanel.repository.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.adminPanel.repository.event.AdminEventJDBCImpl;

@Repository


public class AdTicketJDBCImpl implements AdTicketJDBC{
	private  static final Logger logger = LoggerFactory.getLogger(AdminEventJDBCImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void addTicketDtosByEventId(Integer eventId,TicketDtos ticketDtos) {
		String sql="""
					insert into ticket(event_id,		ticket_name,	ticket_price,
									   ticket_quantity,	ticket_remaining)
					values(?,?,?,?,?)
			
				""".trim();
		try {
				jdbcTemplate.update(sql,
									eventId,ticketDtos.getTicketSection(),ticketDtos.getTicketPrice(),
									ticketDtos.getTicketQuantity(),ticketDtos.getTicketQuantity());
			
		} catch (Exception e) {
			logger.info("添加addTicketDtosByEventId有錯誤"+e.getMessage());
			throw new RuntimeException("添加addTicketDtosByEventId有錯誤"+e.getMessage());		}
		
		
		
	}

	
	
	@Override
	public void updateTicketDtosByEventId(Integer eventId, TicketDtos ticketDtos) {
		String sql="""
					update 	ticket
					set	   	ticket_price 	=?,
						   	ticket_quantity =?
					where 	event_id 	=? and ticket_name =?		
				""".trim();
		try {
			int row=jdbcTemplate.update(sql,
										ticketDtos.getTicketPrice(),ticketDtos.getTicketQuantity(),
										eventId,ticketDtos.getTicketSection());
			if(row<1) {
				logger.info("updateTicketDtosByEventId更新筆數為0");
				throw new RuntimeException("updateTicketDtosByEventId更新筆數為0");}
									
		} catch (Exception e) {
			logger.info("updateTicketDtosByEventId更新失敗，有錯誤",e.getMessage());
			throw new RuntimeException("updateTicketDtosByEventId更新失敗，有錯誤"+e.getMessage());		
		}
		
	}



	
	@Override
	public void updateRemaining(Integer eventId, Integer tickets,String section) {
		
		String sql="""
					update ticket 
					set	   ticket_remaining=ticket_remaining + ?
					where  event_id=?
					and    ticket_name=?
				""".trim();

		try {
				jdbcTemplate.update(sql,tickets,eventId,section);
		} catch (Exception e) {
			logger.warn("更新餘票失敗"+e.getMessage());
			throw new RuntimeException("更新餘票失敗"+e.getMessage());
			
			
		}
	}

	
	
	
	
	
	
}
