package com.example.demo.adminPanel.repository.event;

import java.util.List;

import com.example.demo.adminPanel.dto.event.AddEventDto;
import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;

public interface AdminEventJDBC {
	
	
	List<GetEventAllDto> findEventAllDto();
	
	
	EventDetailDto findEventDetailById(Integer eventId);

	
	List<TicketDtos> findTicketDtosById(Integer eventId);
	
	int addEventDto(EventDetailDto dto,Integer hostId);
	
	void addSalesStatus(Integer eventId);
	
}
