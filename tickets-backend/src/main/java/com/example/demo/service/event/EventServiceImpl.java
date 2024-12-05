package com.example.demo.service.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	@Cacheable(key = CacheKeys.Event.ALL_EVENTPIC)
	public List<EventPicDto> findAllEventPic() {

		return eventRespositoryJdbc.findAllEventPics();
		
	}

	// 根據活動名稱查詢活動 ID
	@Override
	@Cacheable(key ="'" +CacheKeys.Event.EVENTID_PREFIX +"' +#Name")
	public Integer findEventId(String Name) {

		return eventRepository.findEventIdByEventName(Name);

	}

	// 根據活動 ID 查詢活動詳細資訊
	@Override
	@Cacheable(key ="'"+ CacheKeys.Event.EVENT_DETAIL_PREFIX +"' +#eventId")
	public EventDto findEventDetails(Integer eventId) {

		return eventRespositoryJdbc.findEventDetailByEventId(eventId).get();

	}


//===================================
	@Override
	@Cacheable(key = "'"+CacheKeys.Event.EVENT_PIC_PREFIX+"'+#eventId")
	public PicDto getPicDto(Integer eventId) {

		
		return eventRespositoryJdbc.findPicByEventId(eventId);
	}



	@Override
	@Cacheable(key = "T(com.example.demo.util.CacheKeys.Event).getSectionQuantityKey(#eventId, #section)")
	public Integer getQuantity(String section, Integer eventId) {
		
	
		return eventRespositoryJdbc.findQuantityByEventIdAndSection(eventId, section);
	}



	@Override
	public Map<Integer, String> checkSeatStatus(Integer eventId, String section) {
		
		
		return eventRespositoryJdbc.checkSeatStatus(eventId, section);
	}
	
	@Cacheable(key="'"+CacheKeys.Event.EVENTNAME_PREFIX+"'+#eventId")
	public String getEventName(Integer eventId) {
		
		return eventRespositoryJdbc.findEventNameByEventId(eventId);
	}
	
	
	
	
	
	

}
