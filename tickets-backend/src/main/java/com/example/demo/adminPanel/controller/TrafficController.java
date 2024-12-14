package com.example.demo.adminPanel.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.service.traffic.TrafficService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/traffic")
@RequiredArgsConstructor
@Slf4j
public class TrafficController {
	private final TrafficService trafficService;
	
	@GetMapping("/block/{userName}")
	public ResponseEntity<ApiResponse<Object>> blockUser(@PathVariable("userName")
											String userName){
		
		trafficService.blockUserName(userName);
		
		return ResponseEntity.ok(ApiResponse.success("封鎖成功", userName));
	}
	
	
	@GetMapping("/unblock/{userName}")
	public ResponseEntity<ApiResponse<Object>> unblockUser(@PathVariable("userName")
											String userName){
		
		trafficService.unblockUserName(userName);
		
		return ResponseEntity.ok(ApiResponse.success("解鎖成功", userName));
	}
	
	@GetMapping("/block/all")
	public ResponseEntity<ApiResponse<Object>> getblockUser(){

		Set<String> name =trafficService.getBlockedUserNames();

		return ResponseEntity.ok(ApiResponse.success("獲得成功", name));
}
	
	
	

}
