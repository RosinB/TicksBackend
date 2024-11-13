package com.example.demo.repository.sales;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.sales.SalesDto;

@Repository
@Qualifier("SalesJDBC")
public class SalesRespositoryJdbcImpl implements SalesRepositoryJdbc{

	@Override
	public Optional<SalesDto> findSalesDetailByEventId(Integer eventId) {
		String sql="""
					select 	s.sales_id as salesId, 
							s.sales_remaining as salesRemaining,
							s.sales_status as salesStatus,
							e.event_id as eventId,
							e.event_total_tickets as eventTotalTickets
							
					from 	sales s
					join 	event e
					on  	s.event_id=e.event_id
					where 	e.event_Id=?			
				
				""".trim();
		
		
		
		return Optional.empty();
	}

	
	
}
