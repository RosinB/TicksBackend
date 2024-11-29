package com.example.demo.service.sales;

import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.SeatStatusDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;

public interface SalesService {

	SalesDto getTickets(Integer eventId);

	TicketSectionDto getTicketSection(Integer eventId,String userName);
	
	
//	==========訂單邏輯==========
	void buyTicket(PostTicketSalesDto data);
	

//===========檢查座位圖===============

	SeatStatusDto checkSeatStatus(Integer eventId,String section) ;
	
}
