package com.example.demo.service.event;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;
import com.example.demo.util.CacheKeys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final EventRespositoryJdbc eventRespositoryJdbc;

	
	
	// 獲取所有活動圖片資訊
	@Override
	@Cacheable(prefixKey = CacheKeys.Event.ALL_EVENTPIC)
	public List<EventPicDto> findAllEventPic() {

		return eventRespositoryJdbc.findAllEventPics();
		
	}

	// 根據活動名稱查詢活動 ID
	@Override
	@Cacheable(prefixKey  =CacheKeys.Event.EVENTID_PREFIX ,key = " #a0")
	public Integer findEventId(String Name) {

		return eventRepository.findEventIdByEventName(Name);

	}

	// 根據活動 ID 查詢活動詳細資訊
	@Override
	@Cacheable(prefixKey =  CacheKeys.Event.EVENT_DETAIL_PREFIX ,key = " #a0")
	public EventDto findEventDetails(Integer eventId) {

		return eventRespositoryJdbc.findEventDetailByEventId(eventId).get();

	}


//===================================,key = "#a0"
	@Override
	@Cacheable(prefixKey = CacheKeys.Event.EVENTID_PREFIX  ,key="#a0")
	public PicDto getPicDto(Integer eventId) {

		
		return eventRespositoryJdbc.findPicByEventId(eventId);
	}



	@Override
	public Integer getQuantity(String section, Integer eventId) {
		
	
		return eventRespositoryJdbc.findQuantityByEventIdAndSection(eventId, section);
	}



	@Override
	public Map<Integer, String> checkSeatStatus(Integer eventId, String section) {
		
		
		return eventRespositoryJdbc.checkSeatStatus(eventId, section);
	}
	
	@Cacheable(prefixKey =CacheKeys.Event.EVENTNAME_PREFIX , key = "#a0")
	public String getEventName(Integer eventId) {
		
		return eventRespositoryJdbc.findEventNameByEventId(eventId);
	}
	
	
	
	
	
	

}
