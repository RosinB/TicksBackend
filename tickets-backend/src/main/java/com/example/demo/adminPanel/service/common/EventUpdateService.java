package com.example.demo.adminPanel.service.common;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.adminPanel.repository.event.AdminEventJDBC;
import com.example.demo.adminPanel.repository.host.AdminHostJDBC;
import com.example.demo.adminPanel.repository.ticket.AdTicketJDBC;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventUpdateService {
	
	private final AdminEventJDBC adminEventJDBC;
	private final EventQueryService eventQueryService;
	private final AdTicketJDBC adTicketJDBC;
	private final AdminHostJDBC adminHostJDBC;
	
    @Transactional
	public void addEvent(EventDetailDto dto) {
		
		Integer eventId=addEventDto(dto);
		addSalesStatus(eventId);
		addTicketDtos(dto.getTicketDtos(),eventId);
		addPic(dto, eventId);
		
		
	}
    @Transactional
    public EventDetailDto updateEvent(EventDetailDto dto) {
        log.info("演唱會更新開始 eventId: {}", dto.getEventId());
        
        setEventStatus(dto);
        
        Integer eventId = dto.getEventId();
        Integer hostId = eventQueryService.getHostId(dto.getHostName());
        
        updateEventDetails(dto, eventId, hostId);
        
        log.info("演唱會更新完成 eventId: {}", dto.getEventId());
        return dto;
    }
    
    private void updateEventDetails(EventDetailDto dto, Integer eventId, Integer hostId) {
        // 更新主要資料
        adminEventJDBC.updateEventDto(dto, hostId, eventId);
        
        // 更新票券資料
        updateTickets(dto.getTicketDtos(), eventId);
        
        // 更新圖片資料
        adminHostJDBC.updatePic(dto, eventId);
    }
    
    private void updateTickets(List<TicketDtos> ticketDtos, Integer eventId) {
        ticketDtos.forEach(ticketDto -> 
            adTicketJDBC.updateTicketDtosByEventId(eventId, ticketDto)
        );
    }
    
    private void setEventStatus(EventDetailDto dto) {
        LocalDate currentDate = LocalDate.now();
        dto.setEventStatus(dto.getEventDate().isAfter(currentDate) ? 
            "即將舉辦" : "已舉辦");
    }
	
    
    
    
	//=================新增event的table=================
	public Integer addEventDto(EventDetailDto dto) {
		
		LocalDate currentDate = LocalDate.now();

        if(dto.getEventDate().isAfter(currentDate)) {
            dto.setEventStatus("即將舉辦");
        }else dto.setEventStatus("已舉辦");
        
        Integer hostId =eventQueryService.getHostId(dto.getHostName());
        
		Integer eventId= adminEventJDBC.addEventDto(dto,hostId);
		
		
		return eventId;

		
	}
	
	//=================新增sales的table=================

	public void addSalesStatus(Integer eventId) {
		
		adminEventJDBC.addSalesStatus(eventId);

		
	}
	//=================新增ticket的table=================

	public void addTicketDtos(List<TicketDtos> ticketDtos,Integer eventId) {
		
		for(TicketDtos ticketDto : ticketDtos) {
			adTicketJDBC.addTicketDtosByEventId(eventId,ticketDto);
		}
		
	}
	//=================新增pic的table=================
	public void addPic(EventDetailDto dto,Integer eventId) {
		
		adminHostJDBC.addPic(dto, eventId);

		
	}
}
