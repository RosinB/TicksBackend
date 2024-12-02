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
								o.order_datetime	as orderUpdate	,
					Group_concat(p.pool_number) 	as poolNumbers
					
					from 		orders o 
					join 		event e
					on 			o.event_id=e.event_id
					join 		users u
					on			o.user_id=u.user_id
					join 		pool p 
					on			o.order_id=p.order_id
					where 		e.event_id =?
					group by 	o.order_id,e.event_id,e.event_performer,u.user_name,o.order_quantity,o.order_section,o.order_status,o.order_datetime
					order by 	o.order_datetime desc
				""".trim();		
		
		try {
			
			List<AdOrdersDto> dtos=jdbcTemplate.query(sql, (rs,rowNum)->{
				
				AdOrdersDto dto= new BeanPropertyRowMapper<>(AdOrdersDto.class).mapRow(rs, rowNum);
			
						String poolNumbers=rs.getString("poolNumbers");
						if(poolNumbers!=null&& !poolNumbers.isEmpty()) {
							String[] numbers=poolNumbers.split(",");
							for(String number:numbers) {
								dto.addPoolNumber(Integer.parseInt(number.trim()));
							}	
						}					
					return dto;	
			},eventId);
			return dtos;
		} catch (Exception e) {
			logger.info("findAllOrdersByEventId查詢失敗"+e.getMessage());
			throw new RuntimeException("findAllOrdersByEventId"+e.getMessage());
		}
		
	
		
	}

	
	
	@Override
	public Integer updateOrderByUpdateTime(Integer eventId,String section) {

		String sql="""
				UPDATE pool p
				JOIN orders o ON p.order_id = o.order_id
				SET  p.order_id = NULL,
				     p.pool_status = '未售出',
				     o.order_status = '訂單取消'  
				WHERE o.order_datetime < DATE_SUB(NOW(), INTERVAL 10 MINUTE)
				AND   o.order_status = '付款中'
				and   o.event_id=? 
				AND  o.order_section = ?
				AND   p.event_section = o.order_section  -- 確保兩個 section 相符
				""".trim();
		
		try {
			Integer tickets=jdbcTemplate.update(sql,eventId,section);
			logger.info("清除{}筆訂單"+tickets);
			Integer updateTickets=tickets/2;
			return updateTickets;
			
	
		} catch (Exception e) {
			logger.warn("updateOrderByUpdateTime錯誤");
			throw new RuntimeException("updateOrderByUpdateTime錯誤");
		}
		
	}
	
	
	
	
	
}
