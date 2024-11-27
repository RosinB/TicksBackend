package com.example.demo.traffic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TrafficStatsController {

    @Autowired
    private TrafficStatsService trafficStatsService;
    
    @GetMapping("/traffic/per-second")
    ResponseEntity<ApiResponse<Object>> getTrafficStatsPerSecond(@RequestParam("start") long startTimestamp,
												            	@RequestParam("end") long endTimestamp)
    { 	
    	return ResponseEntity.ok(ApiResponse.success("傳達成功", 
    							trafficStatsService.getTrafficStatsPerSecond(startTimestamp, endTimestamp)));
    }

}