package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.service.event.EventService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/event")
public class EventController {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);
	
	
	@Autowired
	EventService eventService;
	
	@GetMapping("/all")
	ResponseEntity<ApiResponse<Object>> getEvent(){		
		List<EventDto> eventDto=eventService.findAllEvent();
		logger.info("進入到/event/all，並查詢到:"+eventDto);
		return ResponseEntity.ok(ApiResponse.success("查詢所有活動成功", eventDto));
	}

	
	//主要列印首頁和List的圖片
	@GetMapping("/ticketAllPic")
	ResponseEntity<ApiResponse<Object>> getEventPic(){
		List<EventPicDto> eventPicDtos=eventService.findAllEventPic();
		
		return ResponseEntity.ok(ApiResponse.success("查詢圖片成功", eventPicDtos));
		
		
		
	}
	
	
	
	//獲得演唱會資訊
	@GetMapping("/ticket")
	ResponseEntity<ApiResponse<Object>> getEventDetails(@RequestParam("eventName") String eventName){
		System.out.println(eventName);
		Optional<EventDto> eventDto=eventService.findEventDetails(eventName);
		logger.info("登入到/event/ticket， 並查詢到:"+eventDto);
		if(eventDto.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, eventName, null));

		
		return ResponseEntity.ok(ApiResponse.success("活動查詢成功", eventDto.get()));

		
	}
		
		
		
	}
	

