package com.example.demo.service.sales;


import com.example.demo.model.dto.sales.SalesDto;

public interface SalesService {

	SalesDto getTickets(Integer eventId);
	
	
}
