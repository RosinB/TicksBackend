package com.example.demo.service.sales;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.model.dto.sales.CheckSectionStatusDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;

import jakarta.transaction.Transactional;


@Service
public class SalesServiceImpl implements SalesService {

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

//	處理購票邏輯
//========================================================================================================
	@Override
	@Transactional
	public String buyTicket(PostTicketSalesDto tickets) {
		Integer eventId = tickets.getEventId();
		Integer quantity = tickets.getQuantity();
		String section = tickets.getSection();
		String userName = tickets.getUserName();
		
	    logger.info("開始處理購票，eventId: {}, section: {}, quantity: {}, userName: {}", eventId, section, quantity, userName);
		salesRepositoryJdbc.checkTicketAndUpdate(section, eventId, quantity);
		
		
		return "購票成功";
	}



	// 獲得演唱會資訊
	@Override
	public SalesDto getTickets(Integer eventId) {

		return salesRepositoryJdbc.findSalesDetailByEventId(eventId);

	}

	// 挑選區域要用的service資訊
	public TicketSectionDto getTicketSection(Integer eventId) {
		TicketSectionDto ticketSectionDto = new TicketSectionDto();
		// 一次查詢
		List<TicketDto> ticketDto = salesRepositoryJdbc.findPriceAndStatusByEventId(eventId);
		// 兩次查詢
		PicDto picDto = eventRespositoryJdbc.findPicByEventId(eventId);

		// 三次查詢
		Optional<EventDto> eventDtos = eventRespositoryJdbc.findEventDetailByEventId(eventId);

		EventDto eventDto = eventDtos.get();

		// 票價id
		ticketSectionDto.setEventId(eventId);

		// 演唱會名字
		ticketSectionDto.setEventName(eventDto.getEventName());

		// 歌手名字
		ticketSectionDto.setEventPerformer(eventDto.getEventPerformer());

		// 演唱會日期
		ticketSectionDto.setEventDate(eventDto.getEventDate());

		// 演唱會時間
		ticketSectionDto.setEventTime(eventDto.getEventTime());
//		System.out.println(eventDto.getEventTime());
		// 地點
		ticketSectionDto.setEventLoaction(eventDto.getEventLocation());

		ticketSectionDto.setHostName(eventDto.getHostName());

		// 票價資訊
		ticketSectionDto.setTicketDto(ticketDto);

		// 圖片
		ticketSectionDto.setTicketPicList(picDto.getPicEventList());

		return ticketSectionDto;

	}

	
	
	//抓取票區剩餘狀態
	public CheckSectionStatusDto getTicketRemaining(String section ,Integer eventId) {
		
		
		CheckSectionStatusDto dto= salesRepositoryJdbc.checkSectionStatus(section, eventId);
		dto.setEventId(eventId);
		return dto;
	}
	
	
}
