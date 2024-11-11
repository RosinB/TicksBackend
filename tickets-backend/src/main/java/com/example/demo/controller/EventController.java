package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.service.event.EventService;
import com.example.demo.util.ApiResponse;

@RestController
@Controller
public class EventController {

	@Autowired
	EventService eventService;
	
	@GetMapping 
	ResponseEntity<ApiResponse<Object>> getEvent(){
		
		List<EventDto> eventDto=eventService.findAllEvent();
		
		
		
		return ResponseEntity.ok(ApiResponse.success("查詢所有活動成功", eventDto));
	}
	
	
}
