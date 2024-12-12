package com.example.demo.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.service.event.EventService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;
	
	
	// 主要列印首頁和List的圖片 home.js
	@GetMapping("/ticketAllPic")
	ResponseEntity<ApiResponse<Object>> getEventPic() {
		
		List<EventPicDto> eventPicDtos = eventService.findAllEventPic();
		return  ResponseEntity.ok()
		        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))  // 設置緩存時間
		        .body(ApiResponse.success("查詢圖片成功", eventPicDtos));

	}

	// 獲得演唱會資訊
	@GetMapping("/ticket/{eventId}")
	ResponseEntity<ApiResponse<Object>> getEventDetails(@PathVariable("eventId") Integer eventId) {
		EventDto eventDto = eventService.findEventDetails(eventId);
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).body(ApiResponse.success("活動查詢成功", eventDto))
			;	

	}
	

}
