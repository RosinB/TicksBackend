package com.example.demo.repository.sales;

import java.util.Optional;

import com.example.demo.model.dto.sales.SalesDto;

public interface SalesRepositoryJdbc {

	Optional<SalesDto> findSalesDetailByEventId(Integer eventId);
	
	
}
