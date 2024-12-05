package com.example.demo.adminPanel.service.event;

import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.LockedDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeDto;
import com.example.demo.adminPanel.dto.ticket.StatusOnSaleDto;
import com.example.demo.adminPanel.repository.event.AdminEventJDBC;

import com.example.demo.adminPanel.service.common.EventQueryService;
import com.example.demo.adminPanel.service.common.EventUpdateService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService{
		

	private final AdminEventJDBC adminEventJDBC;
	private final EventQueryService eventQueryService;
	private final EventUpdateService eventUpdateService;
	
	//找全部演唱會的簡單資訊
	@Override
	public List<GetEventAllDto> getAllEvent() {
		
		return adminEventJDBC.findEventAllDto();
	}


	//找演唱會透過id
	@Override
	public EventDetailDto getEventById(Integer eventId) {
	
		return eventQueryService.getEventDetailDto(eventId);
	}

	@Override
	@Transactional
	public void addEvent(EventDetailDto dto) {
     
		eventUpdateService.addEvent(dto);
	
	}
	
	@Override
	public EventDetailDto updateEvent(EventDetailDto dto) {
	  
		return eventUpdateService.updateEvent(dto);

	}

	@Override
	public List<StatusOnSaleDto> getStatusOnSale() {
	
		return adminEventJDBC.findStatusOnSale();
	}

	//查詢票務實時狀態
	@Override
	public RealTimeDto getRealTimeDto(Integer eventId) {
	
		return eventQueryService.getRealTimeDto(eventId);
	}
	
	@Override
	public void LockTicket(LockedDto lock) {
		adminEventJDBC.updateStatus(lock);
		
	}
	
}
