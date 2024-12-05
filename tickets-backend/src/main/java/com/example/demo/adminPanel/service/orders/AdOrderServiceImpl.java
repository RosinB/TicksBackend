package com.example.demo.adminPanel.service.orders;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdOrderServiceImpl implements AdOrdersService{

	
	
	private final AdOrdersJDBC adOrdersJDBC;
	
	@Override
	public List<AdOrdersDto> getAllOrdersByEventId(Integer eventId) {
		
		return adOrdersJDBC.findAllOrdersByEventId(eventId);
		
	}

	
	
	
	
}
