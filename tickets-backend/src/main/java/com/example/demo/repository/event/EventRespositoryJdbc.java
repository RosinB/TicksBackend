package com.example.demo.repository.event;

import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;

public interface EventRespositoryJdbc {
	
	
	Optional<EventDto> findEventDetail(String eventName);
	

}
