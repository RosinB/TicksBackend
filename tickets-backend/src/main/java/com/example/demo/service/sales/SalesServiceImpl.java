package com.example.demo.service.sales;

import java.util.List;
import java.util.concurrent.TimeUnit;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.common.config.RabbitMQConfig;
import com.example.demo.common.exception.UserIsNotVerifiedException;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.SeatStatusDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.service.common.SeatService;
import com.example.demo.service.common.TicketStockService;
import com.example.demo.service.event.EventService;
import com.example.demo.service.order.OrderService;
import com.example.demo.service.user.UserService;
import com.example.demo.util.CacheKeys;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesServiceImpl implements SalesService {

	private final SalesRepositoryJdbc salesRepositoryJdbc;

	private final RabbitTemplate rabbitTemplate;
	private final TicketStockService ticketStockService;
	private final UserService userService;
	private final SeatService seatService;
	private final OrderService orderSerivce;
	private final EventService eventService;
	
	public SalesServiceImpl(SalesRepositoryJdbc salesRepositoryJdbc, 
			TicketStockService ticketStockService,
			RabbitTemplate rabbitTemplate, 
			Jackson2JsonMessageConverter messageConverter, 
			UserService userService,
			SeatService seatService, 
			OrderService orderSerivce,
			EventService eventService) {
		this.ticketStockService = ticketStockService;
		this.salesRepositoryJdbc = salesRepositoryJdbc;
		this.seatService = seatService;
		this.rabbitTemplate = rabbitTemplate;
		this.userService = userService;
		this.orderSerivce = orderSerivce;
		this.eventService=eventService;
		this.rabbitTemplate.setMessageConverter(messageConverter);

	}
	
//	處理購票邏輯
//========================================================================================================
	@Override
	@Transactional
	public void buyTicket(PostTicketSalesDto tickets) {
		Integer eventId = tickets.getEventId();
		String section = tickets.getSection();
		Integer quantity = tickets.getQuantity();
		
		try {

			Integer currentStock = ticketStockService.ensureStockInRedis(eventId, section);

			if (currentStock < quantity) {
				throw new RuntimeException("庫存不足，購票失敗！當前庫存:" + currentStock);
			}

			Long remainingStock = ticketStockService.decrementTicketStock(eventId, section, quantity);
			
			rabbitTemplate.convertAndSend(
					RabbitMQConfig.EXCHANGE_NAME, 
					RabbitMQConfig.STOCK_UPDATE_ROUTING_KEY,
					tickets);

			log.info("購票成功，剩餘庫存：{}，RequestID: {}", remainingStock, tickets.getRequestId());
			
		} catch (Exception e) {

			ticketStockService.rollbackTicketStock(eventId, section, quantity);
			throw new RuntimeException("購票處理失敗：" + e.getMessage(), e);

		}
	}

	@Override
	@Transactional
	public void buyTicketWithSeat(PostTicketSalesDto tickets) {
		Integer eventId = tickets.getEventId();
		Integer quantity = tickets.getPoolNumber().length;
		String section = tickets.getSection();
		String requestId = tickets.getRequestId();
		Integer[] seats = tickets.getPoolNumber();

		// 獲取用戶 ID
		Integer userId = userService.getUserId(tickets.getUserName());

		// 驗證座位
		seatService.validateSeats(tickets.getPoolNumber(), tickets.getSection(), tickets.getEventId());

		// 印出訂單
		Integer orderId = orderSerivce.createOrder(userId, section, eventId, quantity, requestId);

		// 更新訂單座位
		seatService.processSeats(seats, userId, section, eventId, orderId, requestId, requestId);

	}
//	處理購票邏輯
//========================================================================================================

	// 獲得演唱會資訊

	@Override
	@Cacheable(prefixKey = CacheKeys.Sales.TICKETS_PREFIX ,key = "#a0")
	public SalesDto getTickets(Integer eventId) {

		return salesRepositoryJdbc.findSalesDetailByEventId(eventId);

	}

	
	// 挑選區域要用的service資訊
	public TicketSectionDto getTicketSection(Integer eventId, String userName) {

		Boolean isVerified = userService.getUserIsVerified(userName);

		if (!isVerified)
			throw new UserIsNotVerifiedException("使用者沒有認證，使用者ID:" + eventId + "使用者名字:" + userName);
		

		List<TicketDto> ticketDto = salesRepositoryJdbc.findPriceAndStatusByEventId(eventId);
	
		PicDto picDto = eventService.getPicDto(eventId);
	
		EventDto eventDto = eventService.findEventDetails(eventId);
	
		
		 return TicketSectionDto.builder()
	            .eventId(eventId)
	            .eventName(eventDto.getEventName())
	            .eventPerformer(eventDto.getEventPerformer())
	            .eventDate(eventDto.getEventDate())
	            .eventTime(eventDto.getEventTime())
	            .eventLoaction(eventDto.getEventLocation())
	            .hostName(eventDto.getHostName())
	            .ticketDto(ticketDto)
	            .ticketPicList(picDto.getPicEventList())
	            .ticketPicSection(picDto.getPicEventSection())
	            .build();
		
		
	}

	
	
	// 確認座位圖情況
	@Override
	public SeatStatusDto checkSeatStatus(Integer eventId, String section) {


			return 
				SeatStatusDto.builder()
					.quantity(eventService.getQuantity(section, eventId))
					.seatStatus(eventService.checkSeatStatus(eventId, section))
					.build();
	}

}
