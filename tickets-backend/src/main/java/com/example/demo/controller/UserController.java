package com.example.demo.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.user.UserService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/user")

public class UserController {

	@Autowired
	UserService userService;
	
	
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getAllUser(){
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", userService.getAllUser()));
	}
	
	
}
