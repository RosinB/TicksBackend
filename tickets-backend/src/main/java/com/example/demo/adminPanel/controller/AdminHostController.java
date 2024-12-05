package com.example.demo.adminPanel.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.host.HostDto;
import com.example.demo.adminPanel.service.host.AdminHostService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/host")
@RequiredArgsConstructor
@Slf4j
public class AdminHostController {

	
	private final AdminHostService adminService;
	
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getHosts(){
		
		List<HostDto> dto=	adminService.getAllHost();
		
		return ResponseEntity.ok(ApiResponse.success("okok",dto));

	}
	
//===========================post專區==========================
	
//	新增主辦
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<Object>> postHost(@RequestBody HostDto data){
		
			adminService.addHost(data);
			
			return ResponseEntity.ok(ApiResponse.success("傳達成功", data));
		
	}

//  更新主辦
	@PostMapping("/update")
	public ResponseEntity<ApiResponse<Object>> postUpdateHost(@RequestBody HostDto data){
		
			adminService.updateHost(data);
			
			return ResponseEntity.ok(ApiResponse.success("更新成功", 1));
	}
	
	
	
}
