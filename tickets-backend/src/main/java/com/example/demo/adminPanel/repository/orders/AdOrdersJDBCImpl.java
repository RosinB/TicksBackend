package com.example.demo.adminPanel.repository.orders;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.dto.orders.RefundSubmit;
import com.example.demo.util.DatabaseUtils;

@Repository
public class AdOrdersJDBCImpl implements AdOrdersJDBC{

	private final static Logger logger=LoggerFactory.getLogger(AdOrdersJDBCImpl.class);
	private final static RowMapper<AdOrdersDto> adOrdersMapper=new BeanPropertyRowMapper<>(AdOrdersDto.class);
	private final static RowMapper<RefundSubmit> refundMapper=new BeanPropertyRowMapper<>(RefundSubmit.class);
	private final static class SQL{
		
		static String FIND_ALL_ORDERS_BY_EVENTID="""
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
		static String UPDATE_ORDER_BY_UPDATETIME="""
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
		static  String FIND_REFUBD_BY_PENDING="""
				select 
				       r.refund_id as refundId,
					   e.event_id as eventId,
					   u.user_id  as userId,
					   o.order_id as orderId,
					   o.order_datetime as orderDateTime,
					   r.refund_status as refundStatus,
					   r.refund_title as refundTitle,
					   r.refund_reason as refundReason,
					   r.created_time as refundTime			
				from refund r
				join orders o on r.order_id=o.order_id
				join users u on o.user_id =u.user_id
				join event e on e.event_id = o.event_id
				
				
				
				
				""".trim(); 
	}
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<AdOrdersDto> findAllOrdersByEventId(Integer eventId) {
		
		return DatabaseUtils.executeQuery(
							"findAllOrdersByEventId", 
							()->jdbcTemplate.query(SQL.FIND_ALL_ORDERS_BY_EVENTID, (rs,rowNum)->{
								
										AdOrdersDto dto= adOrdersMapper.mapRow(rs, rowNum);
							
										String poolNumbers=rs.getString("poolNumbers");
										if(poolNumbers!=null&& !poolNumbers.isEmpty()) {
											String[] numbers=poolNumbers.split(",");
											for(String number:numbers) {
												dto.addPoolNumber(Integer.parseInt(number.trim()));
											}	
										}					
									return dto;	
								},eventId), 
							String.format("查詢所有訂單錯物 活動ID:eventID:%d",eventId));
		
	}

	
	
	@Override
	public Integer updateOrderByUpdateTime(Integer eventId,String section) {

		return DatabaseUtils.executeQuery(
					"updateOrderByUpdateTime", 
					()->{
						Integer tickets=jdbcTemplate.update(SQL.UPDATE_ORDER_BY_UPDATETIME,eventId,section);
						logger.info("清除訂單比數:"+tickets/2);
						return tickets/2;}, 
					String.format("清除訂單錯誤: eventId:%d", eventId));
	
	}



	@Override
	public List<RefundSubmit> findRefundByPending() {


		
		return  DatabaseUtils.executeQuery("findRefundByPending", 
										()->jdbcTemplate.query(SQL.FIND_REFUBD_BY_PENDING, refundMapper),
										"查詢所有退票請求失敗");
	}
	
	
	
}
