package com.example.demo.service.sales;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.repository.sales.SalesRepositoryJdbc;

@Service
public class SalesServiceImpl  implements SalesService{

	@Autowired
	@Qualifier("SalesJDBC")
	SalesRepositoryJdbc salesRepositoryJdbc;
	
	
	@Override
	public SalesDto getTickets(Integer eventId) {

		
		
		
		return 	salesRepositoryJdbc.findSalesDetailByEventId(eventId);

	}
	
	

}
