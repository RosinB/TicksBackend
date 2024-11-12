package com.example.demo.repository.event;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;

public interface EventRespositoryJdbc {
	
	
	Optional<EventDto> findEventDetailByEventName(String eventName);
	
	List<EventPicDto> findAllEventPics();
}
