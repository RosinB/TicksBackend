package com.example.demo.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("sales")
public class SalesController {

	@GetMapping("/goticket/{eventId}")
	ResponseEntity<ApiResponse<Object>> getAllTickets(@PathVariable("eventId") Integer eventId){
		
		
		
		
		
		return null;
	}
}
