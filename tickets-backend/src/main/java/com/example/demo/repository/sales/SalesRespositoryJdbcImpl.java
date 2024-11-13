package com.example.demo.repository.sales;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.sales.SalesDto;

@Repository
@Qualifier("SalesJDBC")
public class SalesRespositoryJdbcImpl implements SalesRepositoryJdbc{

	private final static Logger logger= LoggerFactory.getLogger(SalesRespositoryJdbcImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public SalesDto findSalesDetailByEventId(Integer eventId) {
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
		
		SalesDto salesDto= jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SalesDto.class),eventId);
		
		if(Optional.of(salesDto).isEmpty() ) {
			logger.info("演唱會Id匹配不到銷售id");
			
			return null;
		}
		
		return salesDto;
	}

	
	
}
