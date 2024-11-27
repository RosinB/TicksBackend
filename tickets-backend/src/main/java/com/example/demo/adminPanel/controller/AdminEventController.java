package com.example.demo.adminPanel.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeDto;
import com.example.demo.adminPanel.dto.ticket.StatusOnSaleDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.adminPanel.repository.event.AdminEventJDBCImpl;
import com.example.demo.adminPanel.service.event.AdminEventService;
import com.example.demo.util.ApiResponse;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/admin/event")
public class AdminEventController {
	
	private  static final Logger logger = LoggerFactory.getLogger(AdminEventJDBCImpl.class);
	
	@Autowired
	AdminEventService adminEventService;
	
	@GetMapping("/all")
	ResponseEntity<ApiResponse<Object>> getAllEvents(){
		
		List<GetEventAllDto> dto=adminEventService.getAllEvent();
		
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	@GetMapping("/{eventId}")
	ResponseEntity<ApiResponse<Object>> getEventById(@PathVariable("eventId") Integer eventId){
		
		EventDetailDto dto = adminEventService.getEventById(eventId);
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	@PostMapping("/add")
	ResponseEntity<ApiResponse<Object>> postAddEvent(@RequestBody EventDetailDto dto){
		logger.info("需要新增的演唱會資料"+dto);
		
		

		String a=adminEventService.addEvent(dto);
		
		return ResponseEntity.ok(ApiResponse.success("傳送成功", a));
	}
	
	//更新資料
	@PostMapping("/update")
	ResponseEntity<ApiResponse<Object>> postUpdateEvent(@RequestBody EventDetailDto dto){
		
		EventDetailDto dtos=adminEventService.updateEvent(dto);
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dtos));
	}
	
	
	@GetMapping("/onsale")
	ResponseEntity<ApiResponse<Object>> getEventOnSale(){
		
	
		List<StatusOnSaleDto> dto = adminEventService.getStatusOnSale();
		
		
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	
	@GetMapping("/onsale/{eventId}")
	ResponseEntity<ApiResponse<Object>> getRealTimeTicket(@PathVariable("eventId") Integer eventId){
		
		RealTimeDto dto=adminEventService.getRealTimeDto(eventId);
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	
	
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handAdminEventRunTimeException(RuntimeException e) {
		logger.info("AdminEvent有RuntimeException:" + e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
	}
	
	
	

}
