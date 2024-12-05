package com.example.demo.adminPanel.service.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.adminPanel.repository.event.AdminEventJDBC;
import com.example.demo.adminPanel.repository.host.AdminHostJDBC;
import com.example.demo.service.event.EventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventQueryService {
	
	private final AdminHostJDBC adminHostJDBC;
	private final AdminEventJDBC adminEventJDBC;
	private final EventService eventService;
	
	public EventDetailDto getEventDetailDto(Integer eventId) {
		EventDetailDto dto=adminEventJDBC.findEventDetailById(eventId);
		List<TicketDtos> ticketDtos=adminEventJDBC.findTicketDtosById(eventId);
		dto.setTicketDtos(ticketDtos);
		
		return dto;
	};
	
	public Integer getHostId(String hostName) {
		
		return adminHostJDBC.findHostIdByHostName(hostName);
	}
	
	public RealTimeDto getRealTimeDto(Integer eventId) {
		
			
		return RealTimeDto.builder()
				.eventId(eventId)
				.eventName(eventService.getEventName(eventId))
				.dto(adminEventJDBC.findRealTimeTicketByEventId(eventId))
				.build()
				;
		
		
	}
	
}
