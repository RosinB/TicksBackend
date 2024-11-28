package com.example.demo.service.sales;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.exception.UserIsNotVerifiedException;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.pic.PicDto;
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
	
	@Autowired
    private RabbitTemplate rabbitTemplate;
	
	 @Autowired
	    public void configureRabbitTemplate(Jackson2JsonMessageConverter messageConverter) {
	        rabbitTemplate.setMessageConverter(messageConverter);
	    }

//	處理購票邏輯
//========================================================================================================
	 @Override
	 @Transactional
	 public void buyTicket(PostTicketSalesDto tickets) {
	     Integer eventId = tickets.getEventId();
	     Integer quantity = tickets.getQuantity();
	     String section = tickets.getSection();
	     String requestId = tickets.getRequestId();
         String stockKey = "event:" + eventId + ":section:" + section + ":stock";
   
  

	     try {
	         
	         Integer remainingStock = redisService.get(stockKey, Integer.class);
	       
	         if (remainingStock == null) {
	             Integer dbStock = salesRepositoryJdbc.findRemaingByEventIdAndSection(eventId, section);
	             if (dbStock == null || dbStock <= 0) {
	                 throw new RuntimeException("該區域的庫存不存在或不足！");
	             }
	             redisService.saveWithExpire(stockKey, dbStock,5,TimeUnit.SECONDS);
	             logger.info("Redis 無庫存記錄，從資料庫初始化，庫存: {}", dbStock);
	         }

	         Long updatedStock = redisService.decrement(stockKey, quantity);
	         logger.info("!===目前剩餘票數: "+updatedStock+" !===");
	         if (updatedStock < 0) {
	             throw new RuntimeException("庫存不足，購票失敗！");
	         }
	               
	         
	            rabbitTemplate.convertAndSend(
	                    RabbitMQConfig.EXCHANGE_NAME,
	                    RabbitMQConfig.STOCK_UPDATE_ROUTING_KEY,
	                    tickets);	            
                      
	           logger.info("扣減成功，剩餘庫存：{}，RequestID: {}", updatedStock, requestId);	             	    	      
	     } catch (Exception e) {
	         logger.error("購票失敗，RequestID: {}，錯誤原因: {}", requestId, e.getMessage());
	         redisService.increment(stockKey, tickets.getQuantity()); // 回滾扣減
	         throw new RuntimeException("購票處理失敗：" + e.getMessage(), e);
	     }
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
			redisService.saveWithExpire(cacheKey, salesDto, 10, TimeUnit.MINUTES);
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
			redisService.saveWithExpire(cacheKey0, isVerified, 10, TimeUnit.MINUTES);
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
			redisService.saveWithExpire(cacheKey2, picDto, 10, TimeUnit.MINUTES);	
			
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

	// 抓取票區剩餘狀態
//	public CheckSectionStatusDto getTicketRemaining(String section, Integer eventId) {
//
//		CheckSectionStatusDto dto = salesRepositoryJdbc.checkSectionStatus(section, eventId);
//		dto.setEventId(eventId);
//		return dto;
//	}

}
