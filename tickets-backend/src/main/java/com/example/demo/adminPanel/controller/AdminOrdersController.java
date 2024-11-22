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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.service.orders.AdOrdersService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrdersController {
	private final static Logger logger=LoggerFactory.getLogger(AdminEventController.class);
	
	@Autowired
	AdOrdersService adOrdersService;
	
	@GetMapping("/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getOrderById(@PathVariable("eventId") Integer eventId){
		

		List<AdOrdersDto> dto=adOrdersService.getAllOrdersByEventId(eventId);
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	
	
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handAdminOrdersRunTimeException(RuntimeException e) {
		logger.info("AdminOrders有RuntimeException:" + e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
	}
	
	
	
	
	
	
	
	
}
