package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.ApiResponse;

@RestController
@Controller
public class EventController {

	@GetMapping 
	ResponseEntity<ApiResponse<Object>> getEvent(){
		
		
		
		
		
		return null;
	}
	
	
}
