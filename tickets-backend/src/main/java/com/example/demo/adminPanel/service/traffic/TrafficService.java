package com.example.demo.adminPanel.service.traffic;

import java.util.List;

import com.example.demo.adminPanel.dto.traffic.TrafficRecordDto;

public interface TrafficService {

	
		List<TrafficRecordDto> getTrafficRecord(Integer eventId,int start, int end);
	
	
}
