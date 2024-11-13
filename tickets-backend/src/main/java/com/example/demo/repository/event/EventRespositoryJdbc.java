package com.example.demo.repository.event;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;

public interface EventRespositoryJdbc {
	
	
	Optional<EventDto> findEventDetailByEventId(String eventId);
	
	List<EventPicDto> findAllEventPics();
}
