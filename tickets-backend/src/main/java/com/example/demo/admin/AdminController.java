package com.example.demo.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.admin.dto.HostDto;
import com.example.demo.admin.service.AdminService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final static Logger logger= LoggerFactory.getLogger(AdminController.class);
	@Autowired
	AdminService adminService;
	
	
	@GetMapping("/hosts")
	ResponseEntity<ApiResponse<Object>> getHosts(){
		
		try {
		List<HostDto> dto=	adminService.getAllHost();
		return ResponseEntity.ok(ApiResponse.success("okok",dto));

		} catch (Exception e) {
			logger.info("抓到host查詢異常");
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
								 .body(ApiResponse
							     .error(400, "查詢host失敗", null));
		}
		
	}
	

	
//===========================post專區==========================
	
//	新增主辦
	@PostMapping("/hosts")
	public ResponseEntity<ApiResponse<Object>> postHost(@RequestBody HostDto data){
		try {
			adminService.addHost(data);
			return ResponseEntity.ok(ApiResponse.success("傳達成功", data));


		} catch (Exception e) {
			logger.info("新增失敗");
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.error(400, "新增失敗", null));
		}
		
		
	}

//  更新主辦
	@PostMapping("/hosts/update")
	public ResponseEntity<ApiResponse<Object>> postUpdateHost(@RequestBody HostDto data){
		
		adminService.updateHost(data);
		
	return ResponseEntity.ok(ApiResponse.success("更新成功", 1));
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
