package com.example.demo.service.event;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.util.CacheKeys;

@Service
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final EventRespositoryJdbc eventRespositoryJdbc;

	public EventServiceImpl(EventRepository eventRepository, EventRespositoryJdbc eventRespositoryJdbc) {
		this.eventRepository = eventRepository;
		this.eventRespositoryJdbc = eventRespositoryJdbc;
	}

	
	
	// 獲取所有活動圖片資訊
	@Override
	@Cacheable(key = CacheKeys.Event.ALL_EVENTPIC, expireTime = 10, timeUnit = TimeUnit.MINUTES)
	public List<EventPicDto> findAllEventPic() {

		return eventRespositoryJdbc.findAllEventPics();
		
	}

	// 根據活動名稱查詢活動 ID
	@Override
	@Cacheable(key = CacheKeys.Event.EVENTID_PREFIX + "{0}", expireTime = 10, timeUnit = TimeUnit.MINUTES)
	public Integer findEventId(String Name) {

		return eventRepository.findEventIdByEventName(Name);

	}

	// 根據活動 ID 查詢活動詳細資訊
	@Override
	@Cacheable(key = CacheKeys.Event.EVENT_DETAIL_PREFIX + "{0}", expireTime = 10, timeUnit = TimeUnit.MINUTES)
	public EventDto findEventDetails(Integer eventId) {

		return eventRespositoryJdbc.findEventDetailByEventId(eventId).get();

	}

}
