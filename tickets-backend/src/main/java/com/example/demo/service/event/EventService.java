package com.example.demo.service.event;

import java.util.List;
import java.util.Map;


import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;

public interface EventService {

	//查詢全部活動

	EventDto findEventDetails(Integer eventId);
	
	Integer findEventId(String Name);


	List<EventPicDto> findAllEventPic();
	
	
	PicDto getPicDto(Integer eventId);
	
	Integer getQuantity(String section,Integer eventId);
	
     Map<Integer, String> checkSeatStatus(Integer eventId, String section) ;

     
 	 String getEventName(Integer eventId) ;

}

