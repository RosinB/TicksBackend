package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.filter.JwtUtil;
import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;
import com.example.demo.service.user.UserService;
import com.example.demo.util.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")

public class UserController {

	@Autowired
	UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	//登入查詢
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Object>> loginUser(@RequestBody LoginDto loginDto) {

		LoginResultDto loginSessionDto = userService.checkUserLogin(loginDto);

		if (!loginSessionDto.getSuccess()) {
			String message = loginSessionDto.getMessage();
			logger.info("使用者登入失敗狀況"+message);
			
			switch (message) {

			case "帳號不存在":
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "帳號不存在", null));
			case "密碼錯誤":
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "密碼錯誤", null));
			}
		}
		
	    String token = JwtUtil.generateToken(loginDto.getUserName());

		
	    Map<String, String> responseBody = new HashMap<>();
	    responseBody.put("token", token);
	    responseBody.put("userName", loginDto.getUserName());
		

	    logger.info("使用者登入成功: " + loginDto.getUserName());
	    return ResponseEntity.ok(ApiResponse.success("登入成功", responseBody));
	}

	//列印出全部User
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getAllUser() {

		return ResponseEntity.ok(ApiResponse.success("查詢成功", userService.getAllUser()));
	}

	//透過token拿取userName去查詢userDto資料
	@GetMapping("/userUpdate")
	public ResponseEntity<ApiResponse<Object>> getUser(@RequestHeader("Authorization") String token){
		token=token.replace("Bearer ", "");
		String userName = JwtUtil.validateToken(token);
		UserDto userDto=userService.getUser(userName);

		return ResponseEntity.ok(ApiResponse.success("查詢單筆成功", userDto));
	}
	
	
	
	
	@PostMapping("/userUpdate")
	public ResponseEntity<ApiResponse<Object>> updateUser( @RequestBody UserUpdateDto userUpdateDto){
		
		String message=userService.updateUser(userUpdateDto);
		
		logger.info(message);
		
		return ResponseEntity.ok(ApiResponse.success("收到", message));
	}
	
	
	
	
	
	
	// User註冊和檢查是否重複
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Object>> addUser(@Valid @RequestBody UserDto userDto) {
		
		
		System.out.println(userDto.getUserName());
		Map<String, String> errors = userService.validateUserInput(userDto);

		if (!errors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "註冊失敗", errors));
		}

		userService.addUser(userDto);
		logger.info("register新增成功");
		return ResponseEntity.ok(ApiResponse.success("新增成功", userDto));
	}

	
}
