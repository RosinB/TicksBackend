package com.example.demo.repository.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.orders.OrderAstractDto;

@Repository
public class OrderRepositoryJdbcImpl implements OrderRepositoryJdbc{

	private final static Logger logger = LoggerFactory.getLogger(OrderRepositoryJdbcImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	
	
	@Override
	public OrderAstractDto findOrderAbstract(Integer eventId, Integer userId) {
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
						where 	o.event_id =? 
						and     o.user_id = ? 
							
					""".trim();
		try {
			System.out.println("我的eventId:"+eventId+"我的userId"+userId);
			OrderAstractDto dto= jdbcTemplate.queryForObject(sql, 
															 new BeanPropertyRowMapper<>(OrderAstractDto.class),
															 eventId,userId);
			System.out.println("訂單摘要查詢成功");
			return dto;
			
			
		} catch (Exception e) {
			logger.info("訂單摘要查詢失敗");
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	
	
	
	
}
