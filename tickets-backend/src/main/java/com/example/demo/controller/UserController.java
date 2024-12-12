package com.example.demo.controller;

import java.util.List;
import java.util.Map;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.filter.JwtUtil;
import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.RefundOrder;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;
import com.example.demo.service.order.OrderService;
import com.example.demo.service.user.UserService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final  JwtUtil jwtUtil;  
	private final UserService userService;
	private final OrderService orderService;

//----------獲取指定用戶的所有訂單詳情
	@GetMapping("/order")
	public ResponseEntity<ApiResponse<Object>> getUserOrder(@RequestHeader("Authorization") String token){
		String userName=jwtUtil.getUserNameFromHeader(token);
		List<OrderDetailDto> dto=orderService.getAllUserOrder(userName);
		return ResponseEntity.ok(ApiResponse.success("查詢成功", dto));
	}

	

//----------獲取所有用戶列表
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getAllUser() {

		
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", userService.getAllUser()));
	}
	

	
//----------根據 token 獲取用戶詳細信息
	@GetMapping("/userUpdate")
	public ResponseEntity<ApiResponse<Object>> getUser(@RequestHeader("Authorization") String token) {
		token = token.replace("Bearer ", "");
	    String userName = jwtUtil.getUsernameFromToken(token);  
		UserDto userDto = userService.getUser(userName);

		return ResponseEntity.ok(ApiResponse.success("查詢單筆成功", userDto));
	}
	
//----------更新使用者
	@PostMapping("/userUpdate")
	public ResponseEntity<ApiResponse<Object>> postUpdateUser(@RequestBody UserUpdateDto userUpdateDto) {

		 userService.updateUser(userUpdateDto);


		return ResponseEntity.ok(ApiResponse.success("收到", "更新成功"));
	}
	
	
	
//----------- 登入查詢
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Object>> postLoginUser(@RequestBody LoginDto loginDto) {

		LoginResultDto loginSessionDto = userService.checkUserLogin(loginDto);
		if (!loginSessionDto.getSuccess()) {
			  return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			            .body(ApiResponse.error(401, loginSessionDto.getMessage(), null));
		}
		
	    String token = jwtUtil.generateToken(loginDto.getUserName());
	    Map<String, String> response = Map.of(
	            "token", token,
	            "userName", loginDto.getUserName()
	        );
		return ResponseEntity.ok(ApiResponse.success("登入成功",  response));
	}



//----------User註冊和檢查是否重複
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Object>> postAddUser( @RequestBody UserDto userDto) {
	    log.info("使用者:{}開始註冊", userDto.getUserName());
	    
		Map<String, String> errors = userService.validateUserInput(userDto);
		
		if (!errors.isEmpty()) {
	        log.warn("使用者:{}註冊失敗, 原因:{}", userDto.getUserName(), errors );
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).
					body(ApiResponse.error(400, "註冊失敗", errors));
		}

		userService.addUser(userDto);

	    log.info("使用者:{}註冊成功", userDto.getUserName());
		return ResponseEntity.ok(ApiResponse.success("新增成功", userDto));
	}
	
	
//----------忘記密碼，檢查用戶名和郵箱	
	@PostMapping("/forget/password")
	public ResponseEntity<ApiResponse<Object>> forgetPassowrd(@RequestParam("userName") String userName,
															  @RequestParam("email") String email){
		
		userService.checkUserAndEmail(userName,email);
		
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", "123yes"));
	}
	
	
//---------忘記密碼，檢查驗證碼
	@GetMapping("/forget/password/{token}")
	public ResponseEntity<ApiResponse<Object>> checkToken(@PathVariable("token") String token){
		
		String userName=userService.checkToken(token);
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", userName));
	}
	
	
	
//----------獲取信箱
	@GetMapping("/email/get/{userName}")
	public ResponseEntity<ApiResponse<Object>> getUserCAPTCHA(@PathVariable("userName") String userName){
		
		String userEmail=userService.getEmail(userName);
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", userEmail));
	}
	
	
//----------獲取驗證碼
	@GetMapping("/email/getCAPTCHA/{userName}")
	public ResponseEntity<ApiResponse<Object>> getUserEmail(@PathVariable("userName") String userName){

		userService.getCAPTCHA(userName);
		
		return ResponseEntity.ok(ApiResponse.success("傳達成功", null));
	}
	
	
//----------郵箱驗證	
	@PostMapping("/email/verification")
	public ResponseEntity<ApiResponse<Object>> verifEmail(@RequestParam("code") String code ,@RequestParam("userName") String userName){
		
		String status=userService.verificationEmail(userName,code);
		
		if(status.equals("驗證成功"))
			return ResponseEntity.ok(ApiResponse.success("傳達成功", status));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, status, "驗證錯誤"));
		
	}
	
	
//---------處理退票相關
	@GetMapping("/order/refund/{orderId}")
	public ResponseEntity<ApiResponse<Object>> reundTicket(@PathVariable("orderId") Integer orderId){
		
		OrderDetailDto dto = orderService.getOrderDetailByOrderId(orderId);
		
		return ResponseEntity.ok(ApiResponse.success("退票成功", dto));
	}
	
	@PostMapping("/order/refund/form")
	public ResponseEntity<ApiResponse<Object>> reundForm(@RequestBody RefundOrder dto){
		
		orderService.refundTicketForm(dto);
		
		return ResponseEntity.ok(ApiResponse.success("退票成功", dto));
	}
	
	
	
	
	
	
	
}
