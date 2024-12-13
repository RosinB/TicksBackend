package com.example.demo.adminPanel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.service.traffic.TrafficService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/traffic")
@RequiredArgsConstructor
@Slf4j
public class TrafficController {
	
	private final TrafficService trafficService;
	
//	
//	@GetMapping("/all")
//	public ResponseEntity<ApiResponse<Object>> getAllTrafficRecord(
//						@RequestParam("start") Integer start,
//						@RequestParam("end") Integer end,
//						@RequestParam("eventId") Integer eventId){
//		List<TrafficRecordDto> dto=
//		trafficService.getTrafficRecord(eventId, start, end);
//		
//		return ResponseEntity.ok(ApiResponse.success("21", dto));
//	}
	
	

}
