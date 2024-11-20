package com.example.demo.service.sales;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.exception.UserIsNotVerifiedException;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.model.dto.sales.CheckSectionStatusDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;

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
	UserRepositoryJdbc userRepositoryJdbc;
	
	
	@Autowired
	@Qualifier("eventJPA")
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RedisService redisService;

	@Autowired
	private RedissonClient redissonClient;

//	處理購票邏輯
//========================================================================================================
	@Override
	@Transactional
	public int buyTicket(PostTicketSalesDto tickets) {
		Integer eventId = tickets.getEventId();
		Integer quantity = tickets.getQuantity();
		String section = tickets.getSection();
		String userName = tickets.getUserName();


		
		String lockKey = "lock:buyTicket:" + tickets.getEventId();
		RLock lock = redissonClient.getLock(lockKey); // 获取分布式锁
	    int orderId = -1;

	    
		try {

			if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
				logger.info("獲得鎖，執行購票邏輯");
				logger.info("開始處理購票，eventId: {}, section: {}, quantity: {}, userName: {}", eventId, section, quantity,
						userName);
				

				salesRepositoryJdbc.checkTicketAndUpdate(section, eventId, quantity);

				String cacheKey = "userId:" + userName;
				Integer userId = redisService.get(cacheKey, Integer.class);
				if (userId == null) {
					userId = userRepository.findIdByUserName(userName);
					redisService.save(cacheKey, userId);
				}

				
				try {
					 orderId = salesRepositoryJdbc.addTicketOrder(userId, section, eventId, quantity);
					
				} catch (Exception e) {
					throw new RuntimeException("處理購票的時候有問題" + e.getMessage());
				}


			} else { 
				System.out.println("未獲得鎖");
			}

		} 
		catch (InterruptedException e) 
		{throw new RuntimeException("獲取鎖失敗", e);}
		finally 
		{ 
			if (lock.isHeldByCurrentThread()) 
			{ 
				lock.unlock(); 
			}
		}
		
		return orderId;

		

	}
//	處理購票邏輯
//========================================================================================================

	// 獲得演唱會資訊
	@Override
	public SalesDto getTickets(Integer eventId) {
		String cacheKey = "tickets:" + eventId;

		SalesDto cachedSalesDto = redisService.get(cacheKey, SalesDto.class);

		if (cachedSalesDto != null)
			return cachedSalesDto;

		try {
			SalesDto salesDto = salesRepositoryJdbc.findSalesDetailByEventId(eventId);
			redisService.saveWithExpire(cacheKey, salesDto, 1, TimeUnit.HOURS);
			return salesDto;

		} catch (Exception e) {
			logger.info("eventId找不到演唱會資訊", eventId);
			throw new RuntimeException("eventId找不到演唱會資訊");
		}

		
	}

	
	
	
	
	
	// 挑選區域要用的service資訊
	public TicketSectionDto getTicketSection(Integer eventId ,String userName) {
		
		String cacheKey0="user:is_verified:"+userName;
		Boolean isVerified= redisService.get(cacheKey0, Boolean.class);
		if(isVerified==null) {
				isVerified=userRepositoryJdbc.findUserIsVerifiedByUserName(userName);
			redisService.save(cacheKey0, isVerified);
		}
		
		if(!isVerified) throw new UserIsNotVerifiedException("使用者沒有認證，使用者ID:"+eventId+"使用者名字:"+userName) ;
		

		
		
		TicketSectionDto ticketSectionDto = new TicketSectionDto();


		String cacheKey2 = "event:pic:" + eventId;

		String cacheKey3 = "event:details:" + eventId;
		
		// 一次查詢
		List<TicketDto> ticketDto = salesRepositoryJdbc.findPriceAndStatusByEventId(eventId) ;
		
			

		// 兩次查詢
		PicDto picDto = redisService.get(cacheKey2, PicDto.class);
		if (picDto == null) {
			picDto = eventRespositoryJdbc.findPicByEventId(eventId);
			redisService.saveWithExpire(cacheKey2, picDto, 1, TimeUnit.HOURS);	
			
		}
		// 三次查詢
		EventDto eventDto = redisService.get(cacheKey3, EventDto.class);
		if (eventDto == null) {
			Optional<EventDto> eventDtoOpt = eventRespositoryJdbc.findEventDetailByEventId(eventId);
			
			if (eventDtoOpt.isEmpty()) 
			{
				throw new RuntimeException("Event details not found for eventId: " + eventId);
			}
			
			eventDto = eventDtoOpt.get();
			redisService.saveWithExpire(cacheKey3, eventDto, 1, TimeUnit.HOURS);
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

		return ticketSectionDto;

	}

	// 抓取票區剩餘狀態
//	public CheckSectionStatusDto getTicketRemaining(String section, Integer eventId) {
//
//		CheckSectionStatusDto dto = salesRepositoryJdbc.checkSectionStatus(section, eventId);
//		dto.setEventId(eventId);
//		return dto;
//	}

}
