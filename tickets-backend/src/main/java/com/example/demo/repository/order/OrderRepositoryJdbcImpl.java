package com.example.demo.repository.order;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;

@Repository
public class OrderRepositoryJdbcImpl implements OrderRepositoryJdbc{

	private final static Logger logger = LoggerFactory.getLogger(OrderRepositoryJdbcImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	
	

	
	@Override
	public List<OrderDetailDto> findOrderDetail(Integer userId) {

		String sql="""  
				select 
						
						o.order_id as orderId,
						o.order_quantity as orderQuantity,
						o.order_section as orderSection,
						o.order_datetime as orderDateTime,
						o.order_status  as orderStatus,
				
						e.event_name as eventName,
						e.event_performer as eventPerformer,
						e.event_date as eventDate,
						e.event_time as  eventTime,
						e.event_location as eventLocation,
						
						t.ticket_price as ticketPrice,
						
						h.host_name as hostName
				
				
				from users u
				
				join orders o on o.user_id = u.user_id
				
				join event e on o.event_id = e.event_id
				
				join host h  on e.host_id = h.host_id
				
				join ticket t on t.ticket_name = o.order_section AND t.event_id = o.event_id
				
				where u.user_id =?
				
				order by o.order_datetime desc


				""".trim();

		try {
			List<OrderDetailDto> dto= jdbcTemplate.query(sql, 
														new BeanPropertyRowMapper<>(OrderDetailDto.class),
														userId);
				
			return dto;

			
		} catch (BadSqlGrammarException e) {
		    logger.error("SQL 語法錯誤: {}", e.getMessage());
		    return null;
		} catch (DataAccessException e) {
		    logger.error("數據訪問錯誤: {}", e.getMessage());
		    return null;

		} catch (Exception e) {
		    logger.error("未知錯誤: {}", e.getMessage());
		    return null;
		}
		
		
	}
	
	
	
//	訂單摘要







	@Override
	public OrderAstractDto findOrderAbstract(Integer orderId) {
			String sql="""
						select o.order_id 		as 	orderId,
							   o.order_quantity as	orderQuantity,
							   o.order_section 	as	orderSection,
							   o.order_status   as  orderStatus,
							   o.order_datetime as  orderDateTime,
							   e.event_name     as  eventName	
						
						from 	orders o
						join 	event e
						on 		o.event_id =e.event_id
						where 	o.order_id =? 
						
							
					""".trim();
		try {
			OrderAstractDto dto= jdbcTemplate.queryForObject(sql, 
															 new BeanPropertyRowMapper<>(OrderAstractDto.class),
															 orderId);
			System.out.println("訂單摘要查詢成功");
			return dto;
			
			
		} catch (Exception e) {
			logger.info("訂單摘要查詢失敗");
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	
	
	
	
}