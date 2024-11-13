package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("sales")
public class SalesController {

	@Autowired
	SalesService salesService;
	
	
	
	
	@GetMapping("/goticket/{eventId}")
	ResponseEntity<ApiResponse<Object>> getAllTickets(@PathVariable("eventId") Integer eventId){
		
		
		SalesDto salesDto=salesService.getTickets(eventId);
		
		if(Optional.of(salesDto).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "查詢失敗 Saels查詢不到", null));	
		}
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", salesDto));
	}
}
