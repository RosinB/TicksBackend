package com.example.demo.service.event;

import java.util.List;

import com.example.demo.model.dto.event.EventDto;

public interface EventService {

	//查詢全部活動
	List<EventDto> findAllEvent();
	
}