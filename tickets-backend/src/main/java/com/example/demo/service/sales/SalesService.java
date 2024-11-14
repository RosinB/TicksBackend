package com.example.demo.service.sales;


import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;

public interface SalesService {

	SalesDto getTickets(Integer eventId);
	
	 TicketSectionDto getTicketSection(Integer eventId);

}
