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


@Service
public class AdminEventServiceImpl implements AdminEventService{
	
private final static Logger logger = LoggerFactory.getLogger(AdminEventServiceImpl.class);
	

	@Autowired
	AdminEventJDBC adminEventJDBC;
	
	@Autowired
	AdminHostJDBC adminHostJDBC;
	
	
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
            dto.setSalesStatus("即將舉辦");
        }else dto.setSalesStatus("已舉辦");
          
   
		adminEventJDBC.addEventDto(dto,hostId);
		
		
		
		
		
		
		
		
		return null;
	}

	
	
	
	
	
}
