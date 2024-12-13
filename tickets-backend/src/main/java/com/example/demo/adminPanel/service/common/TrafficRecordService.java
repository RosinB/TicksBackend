package com.example.demo.adminPanel.service.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.common.websocket.RequestLogWebSocketHandler;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.example.demo.util.traffic.ScalperUtils;
import com.example.demo.util.traffic.TrafficUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficRecordService {
	private final RequestLogWebSocketHandler requestLogWebSocketHandler;
	private final TrafficUtils trafficUtils;
	private final ScalperUtils scalperUtils;
	
	public void processTraffic(TrafficDto trafficData) {
		if (isWebSocketConnection(trafficData)) {
			return;
		}
		
		String userName=trafficData.getUserName();
		trafficUtils.recordBasicTraffic(trafficData);
		recordUserBehavior(trafficData);
		trafficUtils.storeTrafficRecord(trafficData);

		//判斷黃牛
		if (scalperUtils.isScalper(userName)) {
			scalperUtils.recordSuspiciousActivity(trafficData);
		}

		
		
		requestLogWebSocketHandler.sendRequestLog(trafficData);

	}

	// 記錄用戶行為
	private void recordUserBehavior(TrafficDto trafficData) {
		String userName = trafficData.getUserName();

		trafficUtils.recordUserIP(userName, trafficData.getIpAddress());
		trafficUtils.recordUserBehaviorDetails(userName, trafficData);
		trafficUtils.updateRequestFrequency(userName);
	}

	
	
	
	

	private boolean isWebSocketConnection(TrafficDto trafficData) {
		return trafficData.getRequestUrl() != null && trafficData.getRequestUrl().startsWith("/ws/");
	}
}