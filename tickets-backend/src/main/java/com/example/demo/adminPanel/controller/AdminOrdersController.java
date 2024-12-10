package com.example.demo.adminPanel.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.dto.orders.RefundSubmit;
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
	
	
	@GetMapping("/refund")
	public ResponseEntity<ApiResponse<Object>> getRefundSubmit(){
		
		List<RefundSubmit> dto =adOrdersService.getAllRefund();
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", dto));
	}
	
	
	
	
	@PostMapping("/reject/{refundId}")
	public ResponseEntity<ApiResponse<Object>> rejectRefund(@PathVariable("refundId") Integer refundId ){
		

			adOrdersService.rejectRefund(refundId);
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", null));
	}
		
	@PostMapping("/success/{refundId}")
	public ResponseEntity<ApiResponse<Object>> successRefund(@PathVariable("refundId") Integer refundId ){
		

			adOrdersService.successRefund(refundId);
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", null));
	}
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
}
