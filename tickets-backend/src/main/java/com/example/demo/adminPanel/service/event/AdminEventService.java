package com.example.demo.adminPanel.service.event;

import java.util.List;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeDto;
import com.example.demo.adminPanel.dto.ticket.StatusOnSaleDto;

public interface AdminEventService {


	List<GetEventAllDto> getAllEvent();


	EventDetailDto getEventById(Integer eventId);
	
	String addEvent(EventDetailDto dto);
	
	EventDetailDto updateEvent(EventDetailDto dto);
	
	List<StatusOnSaleDto> getStatusOnSale();
	
	RealTimeDto getRealTimeDto(Integer eventId);

}
