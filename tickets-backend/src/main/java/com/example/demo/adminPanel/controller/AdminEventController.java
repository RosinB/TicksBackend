package com.example.demo.adminPanel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/admin/event")
public class AdminEventController {
	

	ResponseEntity<ApiResponse<Object>> getAllEvents(){
		
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", 123));
	}
	

}
