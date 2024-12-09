package com.example.demo.adminPanel.service.orders;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;

public interface AdOrdersService {

	
		List<AdOrdersDto> getAllOrdersByEventId(Integer eventId);
		
		
		
}
