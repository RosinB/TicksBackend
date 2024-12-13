package com.example.demo.util.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScalperUtils {
	private final RedisService redisService;
	
	public boolean isScalper(String userName) {
		return checkMultipleIPs(userName) || checkHighFrequency(userName);
	}

	
	
	
	private boolean checkMultipleIPs(String userName) {
		Set<String> userIps = redisService.sMembers(CacheKeys.util.USERS_IPS + userName);
		return userIps.size() > 3;
	}

	private boolean checkHighFrequency(String userName) {
		Integer requestCount = redisService.get(CacheKeys.util.REQUEST_FREQUENCY + userName, Integer.class);
		return requestCount != null && requestCount > 100;
	}

	
	
	public void recordSuspiciousActivity(TrafficDto trafficData) {
		String suspiciousKey = CacheKeys.util.SUSPICIOUS_USERS + trafficData.getUserName();
		Map<String, Object> suspiciousInfo = new HashMap<>();
		suspiciousInfo.put("lastDetected", System.currentTimeMillis());
		suspiciousInfo.put("ip", trafficData.getIpAddress());
		suspiciousInfo.put("reason", getSuspiciousReason(trafficData));

		redisService.hashMultiSet(suspiciousKey, suspiciousInfo);
		redisService.expire(suspiciousKey, 24, TimeUnit.HOURS);

		log.warn("Detected suspicious user: {}", trafficData.getUserName());
	}

	private String getSuspiciousReason(TrafficDto trafficData) {
		List<String> reasons = new ArrayList<>();
		if (checkMultipleIPs(trafficData.getUserName()))
			reasons.add("多IP訪問");
		if (checkHighFrequency(trafficData.getUserName()))
			reasons.add("訪問頻率異常");
		if (trafficData.isProxy())
			reasons.add("使用代理IP");
		if (trafficData.isRobot())
			reasons.add("機器人特徵");
		return String.join("; ", reasons);
	}
	
}
