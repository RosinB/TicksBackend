package com.example.demo.adminPanel.repository.orders;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;

@Repository
public class AdOrdersJDBCImpl implements AdOrdersJDBC{

	private final static Logger logger=LoggerFactory.getLogger(AdOrdersJDBCImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<AdOrdersDto> findAllOrdersByEventId(Integer eventId) {
		String sql="""
					select  	o.order_id  		AS orderId,
								e.event_name 		As eventName,
								e.event_performer 	As eventPerformer,
								u.user_name			As userName,
								o.order_quantity	AS orderQuantity,
								o.order_section 	as orderSection,
								o.order_status		as orderStatus,
								o.order_datetime	as orderUpdate	
							
					
					from 		orders o 
					join 		event e
					on 			o.event_id=e.event_id
					join 		users u
					on			o.user_id=u.user_id
					where 		e.event_id =?
				""".trim();		
		
		try {
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdOrdersDto.class),eventId);
			
		} catch (Exception e) {
			logger.info("findAllOrdersByEventId查詢失敗"+e.getMessage());
			throw new RuntimeException("findAllOrdersByEventId"+e.getMessage());
		}
		
	
		
	}
	
	
	
	
	
}
