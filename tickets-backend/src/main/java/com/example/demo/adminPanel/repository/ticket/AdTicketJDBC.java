package com.example.demo.adminPanel.repository.ticket;

import com.example.demo.adminPanel.dto.ticket.TicketDtos;

public interface AdTicketJDBC {

	void addTicketDtosByEventId(Integer eventId,TicketDtos ticketDtos);
	
	void updateTicketDtosByEventId(Integer eventId,TicketDtos ticketDtos) ;

	void updateRemaining(Integer eventId,Integer tickets,String section);
	
	
}
