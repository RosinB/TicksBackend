package com.example.demo.service.sales;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.dto.sales.SalesDto;

@Service
public class SalesServiceImpl  implements SalesService{

	@Override
	public Optional<SalesDto> getTickets(Integer eventId) {

		
		
		return Optional.empty();
	}
	
	

}
