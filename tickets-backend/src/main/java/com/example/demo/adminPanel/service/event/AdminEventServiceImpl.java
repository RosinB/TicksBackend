package com.example.demo.adminPanel.service.event;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.adminPanel.repository.event.AdminEventJDBC;
import com.example.demo.adminPanel.repository.host.AdminHostJDBC;
import com.example.demo.adminPanel.repository.ticket.AdTicketJDBC;


@Service
public class AdminEventServiceImpl implements AdminEventService{
	
private final static Logger logger = LoggerFactory.getLogger(AdminEventServiceImpl.class);
	

	@Autowired
	AdminEventJDBC adminEventJDBC;
	
	@Autowired
	AdminHostJDBC adminHostJDBC;
	
	@Autowired
	AdTicketJDBC adTicketJDBC;
	
	//找全部演唱會的簡單資訊
	@Override
	public List<GetEventAllDto> getAllEvent() {
		
		
		
		return adminEventJDBC.findEventAllDto();
	}


	//找演唱會透過id
	@Override
	public EventDetailDto getEventById(Integer eventId) {

		EventDetailDto dto=adminEventJDBC.findEventDetailById(eventId);
		List<TicketDtos> ticketDtos=adminEventJDBC.findTicketDtosById(eventId);
		dto.setTicketDtos(ticketDtos);
		
		return dto;
	}


	@Override
	@Transactional
	public String addEvent(EventDetailDto dto) {
        LocalDate currentDate = LocalDate.now();

        int hostId=adminHostJDBC.findHostIdByHostName(dto.getHostName());
        
        if(dto.getEventDate().isAfter(currentDate)) {
            dto.setEventStatus("即將舉辦");
        }else dto.setEventStatus("已舉辦");
          
//=================新增event的table=================
		Integer eventId= adminEventJDBC.addEventDto(dto,hostId);
		logger.info("新增的演唱會資訊的eventId是:"+eventId);		
//=================新增sales的table=================
		adminEventJDBC.addSalesStatus(eventId);
//=================新增ticket的table=================
		for(TicketDtos ticketDtos :dto.getTicketDtos()) {
			adTicketJDBC.addTicketDtosByEventId(eventId,ticketDtos);
		}
//=================新增pic的table=================
		adminHostJDBC.addPic(dto, eventId);
		
	
		
		return "新增成功";
	}

	@Override
	@Transactional
	public EventDetailDto updateEvent(EventDetailDto dto) {
		logger.info("演唱會更新開始----->演唱會資料:"+dto);

		Integer eventId=dto.getEventId();
        LocalDate currentDate = LocalDate.now();

        if(dto.getEventDate().isAfter(currentDate))
        	dto.setEventStatus("即將舉辦");
        else dto.setEventStatus("已舉辦");
       
        int hostId=adminHostJDBC.findHostIdByHostName(dto.getHostName());
//=================更新eventDto=========================
        adminEventJDBC.updateEventDto(dto, hostId, eventId);
//=================更新ticketDto=========================

        for(TicketDtos ticketDtos :dto.getTicketDtos()) {
			adTicketJDBC.updateTicketDtosByEventId(eventId,ticketDtos);
		}
//=================更新picDto=========================
        adminHostJDBC.updatePic(dto, eventId);
        
		return dto;
	}
	
	
	
	
}
