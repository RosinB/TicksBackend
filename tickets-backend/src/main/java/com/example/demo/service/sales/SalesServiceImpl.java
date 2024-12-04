package com.example.demo.service.sales;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.service.common.TicketStockService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesServiceImpl implements SalesService {

	private final SalesRepositoryJdbc salesRepositoryJdbc;
	private final EventRespositoryJdbc eventRespositoryJdbc;
	private final UserRepositoryJdbc userRepositoryJdbc;
	private final UserRepository userRepository;
	private final RedisService redisService;
	private final RabbitTemplate rabbitTemplate;
	private final TicketStockService ticketStockService;

	public SalesServiceImpl(SalesRepositoryJdbc salesRepositoryJdbc, TicketStockService ticketStockService,
			EventRespositoryJdbc eventRespositoryJdbc, UserRepositoryJdbc userRepositoryJdbc,
			UserRepository userRepository, RedisService redisService, RabbitTemplate rabbitTemplate,
			Jackson2JsonMessageConverter messageConverter) {
		this.ticketStockService = ticketStockService;
		this.salesRepositoryJdbc = salesRepositoryJdbc;
		this.eventRespositoryJdbc = eventRespositoryJdbc;
		this.userRepository = userRepository;
		this.userRepositoryJdbc = userRepositoryJdbc;
		this.rabbitTemplate = rabbitTemplate;
		this.redisService = redisService;
		this.rabbitTemplate.setMessageConverter(messageConverter);

	}

//	處理購票邏輯
//========================================================================================================
	@Override
	@Transactional
	public void buyTicket(PostTicketSalesDto tickets) {
	
		try {
			 Integer currentStock = ticketStockService.ensureStockInRedis(
			            tickets.getEventId(), 
			            tickets.getSection()
			        );
			 
			if (currentStock < tickets.getQuantity()) {
				throw new RuntimeException("庫存不足，購票失敗！當前庫存:" + currentStock);
			}

			 Long remainingStock = ticketStockService.decrementTicketStock(
			            tickets.getEventId(), 
			            tickets.getSection(), 
			            tickets.getQuantity()
			        );
			rabbitTemplate.convertAndSend(
					RabbitMQConfig.EXCHANGE_NAME, 
					RabbitMQConfig.STOCK_UPDATE_ROUTING_KEY,
					tickets
					);

			log.info("購票成功，剩餘庫存：{}，RequestID: {}", remainingStock, tickets.getRequestId());
		} catch (Exception e) {
			
			 ticketStockService.rollbackTicketStock(
			            tickets.getEventId(), 
			            tickets.getSection(), 
			            tickets.getQuantity()
			        );			
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
		String cacheKey = "userId:" + tickets.getUserName();
		Integer userId = redisService.get(cacheKey, Integer.class);
		if (userId == null) {
			userId = userRepository.findIdByUserName(tickets.getUserName());
			redisService.saveWithExpire(cacheKey, userId, 10, TimeUnit.MINUTES);
		}

		for (Integer seat : seats) {

			boolean isAvailable = salesRepositoryJdbc.existsSeatsByPoolNumber(seat, section, eventId);
			if (!isAvailable) {
				log.error("座位 {} 已被購買或不存在", seat);
				throw new RuntimeException("選擇的座位已有人坐或不存在");
			}
		}
		Integer orderId = salesRepositoryJdbc.addTicketOrderWithSeat(userId, section, eventId, quantity, requestId,
				quantity);

		for (Integer seat : seats) {
			log.info("座位號: {}", seat);
			try {
				salesRepositoryJdbc.updateTicketOrderSeat(userId, section, eventId, seat, orderId);
				redisService.saveWithExpire("order:" + requestId, tickets.getUserName(), 10, TimeUnit.MINUTES);
			} catch (Exception e) {
				log.error("訂單新增失敗");
				throw new RuntimeException("訂單新增失敗");

			}

		}

	}
//	處理購票邏輯
//========================================================================================================

	// 獲得演唱會資訊
	
	@Override
	@Cacheable(key = CacheKeys.Sales.TICKETS_PREFIX+"{0}" ,expireTime = 10,timeUnit = TimeUnit.MINUTES)
	public SalesDto getTickets(Integer eventId) {
	
			return salesRepositoryJdbc.findSalesDetailByEventId(eventId);
		
	}

	// 挑選區域要用的service資訊
	public TicketSectionDto getTicketSection(Integer eventId, String userName) {

		String cacheKey0 = "user:is_verified:" + userName;
		Boolean isVerified = redisService.get(cacheKey0, Boolean.class);
		if (isVerified == null) {
			isVerified = userRepositoryJdbc.findUserIsVerifiedByUserName(userName);
			redisService.saveWithExpire(cacheKey0, isVerified, 10, TimeUnit.MINUTES);
		}

		if (!isVerified)
			throw new UserIsNotVerifiedException("使用者沒有認證，使用者ID:" + eventId + "使用者名字:" + userName);

		TicketSectionDto ticketSectionDto = new TicketSectionDto();

		String cacheKey2 = "event:pic:" + eventId;

		String cacheKey3 = "event:details:" + eventId;

		// 一次查詢
		List<TicketDto> ticketDto = salesRepositoryJdbc.findPriceAndStatusByEventId(eventId);

		// 兩次查詢
		PicDto picDto = redisService.get(cacheKey2, PicDto.class);
		if (picDto == null) {
			picDto = eventRespositoryJdbc.findPicByEventId(eventId);
			redisService.saveWithExpire(cacheKey2, picDto, 10, TimeUnit.MINUTES);

		}
		// 三次查詢
		EventDto eventDto = redisService.get(cacheKey3, EventDto.class);
		if (eventDto == null) {
			Optional<EventDto> eventDtoOpt = eventRespositoryJdbc.findEventDetailByEventId(eventId);

			if (eventDtoOpt.isEmpty()) {
				throw new RuntimeException("Event details not found for eventId: " + eventId);
			}

			eventDto = eventDtoOpt.get();
			redisService.saveWithExpire(cacheKey3, eventDto, 10, TimeUnit.MINUTES);
		}

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
		// 地點
		ticketSectionDto.setEventLoaction(eventDto.getEventLocation());

		ticketSectionDto.setHostName(eventDto.getHostName());

		// 票價資訊
		ticketSectionDto.setTicketDto(ticketDto);

		// 圖片
		ticketSectionDto.setTicketPicList(picDto.getPicEventList());

		ticketSectionDto.setTicketPicSection(picDto.getPicEventSection());

		return ticketSectionDto;

	}

	// 確認座位圖情況
	@Override
	public SeatStatusDto checkSeatStatus(Integer eventId, String section) {
		SeatStatusDto dto = new SeatStatusDto();

		String cacheKey = "eventId" + section;

		Integer quantity = redisService.get(cacheKey, Integer.class);

		if (quantity == null) {
			quantity = eventRespositoryJdbc.findQuantityByEventIdAndSection(eventId, section);
			redisService.saveWithExpire(cacheKey, quantity, 30, TimeUnit.MINUTES);
		}

		dto.setQuantity(quantity);

		dto.setSeatStatus(eventRespositoryJdbc.checkSeatStatus(eventId, section));

		return dto;
	}

}
