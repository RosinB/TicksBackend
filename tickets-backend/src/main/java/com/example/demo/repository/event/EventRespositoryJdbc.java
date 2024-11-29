package com.example.demo.repository.event;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;

public interface EventRespositoryJdbc {
	
	
	Optional<EventDto> findEventDetailByEventId(Integer eventId);
	
	List<EventPicDto> findAllEventPics();
	
	PicDto findPicByEventId(Integer eventId);
	
	String findEventNameByEventId(Integer eventId);
	
	//找總票數
	Integer findQuantityByEventIdAndSection(Integer eventId,String section);
	
	Map<Integer,Boolean> checkSeatStatus(Integer eventId,String section);
	
	
}
