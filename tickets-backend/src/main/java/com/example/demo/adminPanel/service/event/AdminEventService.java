package com.example.demo.adminPanel.service.event;

import java.util.List;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;

public interface AdminEventService {


	List<GetEventAllDto> getAllEvent();


	EventDetailDto getEventById(Integer eventId);
	
	String addEvent(EventDetailDto dto);
	
	 EventDetailDto updateEvent(EventDetailDto dto);

}
