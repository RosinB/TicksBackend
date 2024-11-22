package com.example.demo.adminPanel.repository.orders;

import java.util.List;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;

public interface AdOrdersJDBC {

	List<AdOrdersDto> findAllOrdersByEventId(Integer eventId);
	
	
}
