package com.example.demo.service.event;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;

public interface EventService {

	//查詢全部活動
	List<EventDto> findAllEvent();

	Optional<EventDto>findEventDetails(String eventName);

	List<EventPicDto> findAllEventPic();
	
}
