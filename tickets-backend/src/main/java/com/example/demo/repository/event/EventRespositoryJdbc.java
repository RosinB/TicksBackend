package com.example.demo.repository.event;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;

public interface EventRespositoryJdbc {
	
	
	Optional<EventDto> findEventDetailByEventId(Integer eventId);
	
	List<EventPicDto> findAllEventPics();
	
	PicDto findPicByEventId(Integer eventId);
	
	String findEventNameByEventId(Integer eventId);
}
