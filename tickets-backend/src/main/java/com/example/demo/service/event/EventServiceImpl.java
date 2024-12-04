package com.example.demo.service.event;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.common.mapper.EventMapper;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.util.ApiResponse;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;

import io.jsonwebtoken.io.IOException;

@Service
public class EventServiceImpl implements EventService {

	private final static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

	@Autowired
	@Qualifier("eventJPA")
	EventRepository eventRepository;

	@Autowired
	EventMapper eventMapper;

	@Autowired
	@Qualifier("eventJDBC")
	EventRespositoryJdbc eventRespositoryJdbc;

	@Autowired
	private RedisService redisService;

	//還沒用到 廢氣中======================
	@Override
	public List<EventDto> findAllEvent() {
		String cacheKey = "allEvents";
		
		List<EventDto> cachedEvents = redisService.get(cacheKey, new TypeReference<List<EventDto>>() {});
		
		if (cachedEvents != null) {
			logger.info("從 Redis 緩存中獲取所有事件列表");
			return cachedEvents;}
		
		List<EventDto> events = eventRepository.findAll().stream().map(eventMapper::toDto).collect(Collectors.toList());

		redisService.saveWithExpire(cacheKey, events, 10, TimeUnit.MINUTES);
		
		return events;
	}
	
	@Override
	public List<EventPicDto> findAllEventPic() {
		String cacheKey = "allEventsPic";
		
		
		redisService.delete(cacheKey);
		List<EventPicDto> cachedEventsPic=redisService.get(cacheKey, new TypeReference<List<EventPicDto>>() {});
		
		if(cachedEventsPic !=null) {
			return cachedEventsPic;
		}	
		
		List<EventPicDto> eventPicDtos= eventRespositoryJdbc.findAllEventPics();
		redisService.saveWithExpire(cacheKey, eventPicDtos, 10, TimeUnit.MINUTES);
		return eventPicDtos;
			
		
	
	}

	
	@Override
	public Integer findEventId(String Name) {
		String cacheKey="eventId"+Name;
		
		Integer cachedEventId=redisService.get(cacheKey, Integer.class);
		if(cachedEventId!=null) {
			return cachedEventId;
		}
		try {
			Integer eventId=eventRepository.findEventIdByEventName(Name);
			return eventId;

		} catch (Exception e) {
			throw new RuntimeException("找活動id發生錯誤",e);
		}	
		
	}

	@Override
	public Optional<EventDto> findEventDetails(Integer eventId) {
		String cacheKey="event:details:"+eventId;

		EventDto cachedEventDto =redisService.get(cacheKey, EventDto.class);
		if(cachedEventDto!=null) {
		    return Optional.of(cachedEventDto);}
		
			Optional<EventDto> eventDtos=eventRespositoryJdbc.findEventDetailByEventId(eventId);

			EventDto eventDto=eventDtos.get();
			redisService.saveWithExpire(cacheKey, eventDto, 10, TimeUnit.MINUTES );
			return eventDtos;
			
		
	}
	

	
	
	
	
	
}
