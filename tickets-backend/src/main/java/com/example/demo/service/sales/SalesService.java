package com.example.demo.service.sales;

import java.util.Optional;

import com.example.demo.model.dto.sales.SalesDto;

public interface SalesService {

	Optional<SalesDto> getTickets(Integer eventId);
	
	
}
