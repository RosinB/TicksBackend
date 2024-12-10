package com.example.demo.adminPanel.service.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficRecordDto;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficServiceImpl implements TrafficService{

	private final RedisService redisService;
	
	@Override
	public List<TrafficRecordDto> getTrafficRecord(Integer eventId,int start, int end) {

		String cachekey=CacheKeys.util.TRAFFIC_RECORD_EVENTID+eventId;
		
	    List<String> records = redisService.listRange(cachekey, start, end);
	
	  
	    return records.stream()
	            .map(record -> {
	                try {
	                    log.info("嘗試轉換記錄: {}", record);
	                    TrafficRecordDto dto = new ObjectMapper().readValue(record, TrafficRecordDto.class);
	                    log.info("轉換成功: {}", dto);
	                    return dto;
	                } catch (Exception e) {
	                    log.error("轉換失敗，原因: {}", e.getMessage());
	                    log.error("失敗的記錄內容: {}", record);
	                    return null;
	                }
	            })
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());
	}

	
	
	
	
}
