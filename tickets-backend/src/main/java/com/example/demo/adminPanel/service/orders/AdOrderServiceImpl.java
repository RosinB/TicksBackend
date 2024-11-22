package com.example.demo.adminPanel.service.orders;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;

@Service
public class AdOrderServiceImpl implements AdOrdersService{

	
	
	@Autowired
	AdOrdersJDBC adOrdersJDBC;
	
	@Override
	public List<AdOrdersDto> getAllOrdersByEventId(Integer eventId) {
		
		
		
		return adOrdersJDBC.findAllOrdersByEventId(eventId);
	}

	
	
	
	
}
