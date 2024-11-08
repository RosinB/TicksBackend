package com.example.demo.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.user.UserDto;
import com.example.demo.service.user.UserService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/user")

public class UserController {

	@Autowired
	UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getAllUser() {

		return ResponseEntity.ok(ApiResponse.success("查詢成功", userService.getAllUser()));
	}

	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Object>> addUser(@RequestBody UserDto userDto) {

		
	    Map<String, String> errors = userService.validateUserInput(userDto);

		if(!errors.isEmpty()) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body(ApiResponse
								 .error(400, "註冊失敗"));

			
		}
	    
	    
	
		userService.addUser(userDto);
		
		logger.info(userDto.toString());
		return ResponseEntity.ok(ApiResponse.success("新增成功", null));
	}

}
