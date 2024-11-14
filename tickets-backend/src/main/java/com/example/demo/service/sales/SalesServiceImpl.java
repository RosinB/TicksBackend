package com.example.demo.service.sales;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;

@Service
public class SalesServiceImpl  implements SalesService{

	private final static Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);
	
	@Autowired
	@Qualifier("SalesJDBC")
	SalesRepositoryJdbc salesRepositoryJdbc;
	
	@Autowired
	@Qualifier("eventJDBC")
	EventRespositoryJdbc eventRespositoryJdbc;
	
	@Autowired 
	@Qualifier("eventJPA")
	EventRepository eventRepository;
	
	
	@Override
	public SalesDto getTickets(Integer eventId) {

		return 	salesRepositoryJdbc.findSalesDetailByEventId(eventId);

	}
	
	//挑選區域要用的service資訊
	public TicketSectionDto getTicketSection(Integer eventId) {
		TicketSectionDto ticketSectionDto = new TicketSectionDto();
		//一次查詢
		List<TicketDto> ticketDto=salesRepositoryJdbc.findPriceAndStatusByEventId(eventId);
		//兩次查詢
		PicDto picDto = eventRespositoryJdbc.findPicByEventId(eventId);
		
		//三次查詢
		System.out.println("//三次查詢");
		Optional<EventDto> eventDtos =eventRespositoryJdbc.findEventDetailByEventId(eventId);
		
		EventDto eventDto = eventDtos.get();
			
		//票價id
		ticketSectionDto.setEventId(eventId);
		
		//演唱會名字
		ticketSectionDto.setEventName(eventDto.getEventName());
		
		//歌手名字
		ticketSectionDto.setEventPerformer(eventDto.getEventPerformer());
		
		//演唱會日期
		ticketSectionDto.setEventDate(eventDto.getEventDate());
		
		//演唱會時間
		ticketSectionDto.setEvetTime(eventDto.getEvenTime());

		//地點
		ticketSectionDto.setEventLoaction(eventDto.getEventLocation());
		
		//票價資訊
		ticketSectionDto.setTicketDto(ticketDto);
		
		//圖片
		ticketSectionDto.setTicketPicList(picDto.getPicEventList());
		
		return ticketSectionDto;
		
	}
	
	
	

}
