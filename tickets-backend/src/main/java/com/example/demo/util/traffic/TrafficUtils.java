package com.example.demo.util.traffic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.example.demo.util.CacheKeys.util;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficUtils {
	private final RedisService redisService;
    private static final long MINUTES = 60;

	public void recordBasicTraffic(TrafficDto trafficData) {
		// 記錄特定活動的流量
		if (trafficData.getEventId() != null) {
			String eventKey = CacheKeys.util.TRAFFIC_EVENTID + trafficData.getEventId();
			redisService.increment(eventKey, 1);
			redisService.expire(eventKey, 5, TimeUnit.MINUTES);
		}

		// 記錄總流量
		String totalKey = CacheKeys.util.TOTAL_DAY_TRAFFIC + LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		redisService.increment(totalKey, 1);
		redisService.expire(totalKey, 24, TimeUnit.HOURS);
	}

	// 存儲詳細記錄 紀錄每個流量的訊息
	public void storeTrafficRecord(TrafficDto trafficData) {
		String recordKey = CacheKeys.util.TRAFFIC_RECORD;
		try {
			String recordJson = new ObjectMapper().writeValueAsString(trafficData);
			redisService.listLeftPush(recordKey, recordJson);
			redisService.listTrim(recordKey, 0, 9999);
		} catch (Exception e) {
			log.error("儲存失敗", e);
		}
	}

	public void recordUserIP(String userName, String ipAddress) {
		String userIpKey = CacheKeys.util.USERS_IPS + userName;
		redisService.sAdd(userIpKey, ipAddress);
		redisService.expire(userIpKey, 30, TimeUnit.MINUTES);
	}

	public void recordUserBehaviorDetails(String userName, TrafficDto trafficData) {
		String behaviorKey = CacheKeys.util.USERS_BEHAVIOR + userName;
		Map<String, Object> behavior = new HashMap<>();
		behavior.put("ip", trafficData.getIpAddress());
		behavior.put("device", trafficData.getDeviceType());
		behavior.put("lastAccess", trafficData.getTimestamp());
		behavior.put("ticketCount", trafficData.getTicketQuantity());
		behavior.put("userAgent", trafficData.getUserAgent());

		redisService.hashMultiSet(behaviorKey, behavior);
		redisService.expire(behaviorKey, 30, TimeUnit.MINUTES);
	}

	 public void updateRequestFrequency(String userName) {
	        String freqKey = CacheKeys.util.REQUEST_FREQUENCY + userName;
	        redisService.increment(freqKey, 1);
	        redisService.expire(freqKey, MINUTES, TimeUnit.SECONDS);
	    }
	
	
	
	
	
	
	
	
	
	
	
	
}
