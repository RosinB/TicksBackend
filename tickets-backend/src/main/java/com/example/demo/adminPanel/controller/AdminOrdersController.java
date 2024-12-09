package com.example.demo.adminPanel.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.service.orders.AdOrdersService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrdersController {
	
	private final AdOrdersService adOrdersService;
	
	@GetMapping("/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getOrderById(@PathVariable("eventId") Integer eventId){
		
		List<AdOrdersDto> dto=adOrdersService.getAllOrdersByEventId(eventId);
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}
	
	
	
	public ResponseEntity<ApiResponse<Object>> getRefundSubmit(){
		
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", null));
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
