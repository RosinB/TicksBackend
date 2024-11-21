package com.example.demo.adminPanel.repository.event;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.event.AddEventDto;
import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;

@Repository
public class AdminEventJDBCImpl implements AdminEventJDBC{
	private  static final Logger logger = LoggerFactory.getLogger(AdminEventJDBCImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	//查詢所有演唱會資訊
	@Override
	public List<GetEventAllDto> findEventAllDto() {
		String sql="""
				select event_id as eventId,
					   event_name as eventName,
					   event_performer as eventPerformer,
					   event_date as eventDate,
					   event_status as eventStatus
				from event
				""".trim();

		try {
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(GetEventAllDto.class));
			
		} catch (Exception e) {
			logger.info("查詢findEventAllDto有錯誤"+e.getMessage());
			throw new RuntimeException("查詢findEventAllDto有錯誤"+e.getMessage());
		}
			
		
	}


	
	
	//透過演唱會ID茶演唱會資料
	@Override
	public EventDetailDto findEventDetailById(Integer eventId) {
		String sql="""
				SELECT			e.event_id 				AS eventId,
							    e.event_performer 		AS eventPerformer,
							    e.event_name 			AS eventName,
							    e.event_description 	AS eventDescription,
							    e.event_date 			AS eventDate,
							    e.event_time 			AS eventTime,
							    e.event_location 		AS eventLocation,
							    e.event_type 			AS eventType,
							    e.event_status			AS eventStatus,							   	   					    
							    h.host_name 			AS hostName,							    
							    s.sales_status 			AS salesStatus,				    
							    p.pic_event_ticket 		AS picEventTicket,
							    p.pic_event_list 		AS picEventList,
							    p.pic_event_section		AS picEventSection
							    
				FROM			event e
				JOIN			host h
				ON				e.host_id = h.host_id
				JOIN			pic p
				ON				e.event_id = p.event_id 
				JOIN 			sales s
				ON				e.event_id=s.event_id			
				WHERE			e.event_id = ?
																			
				""".trim();
		try {
			
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EventDetailDto.class),eventId);		
			
		} catch (Exception e) {
			logger.info("findEventDetailById資料庫錯誤找不到",e.getMessage());
			throw new RuntimeException("findEventDetailById資料庫錯誤找不到"+e.getMessage());
		}
		
		
		
	}




	
	//透過演唱會ID查ticketDtos
	@Override
	public List<TicketDtos> findTicketDtosById(Integer eventId) {
		String sql="""
				select 		t.ticket_price as ticketPrice,
							t.ticket_name as ticketSection,
							t.ticket_quantity as ticketQuantity,
							t.ticket_remaining as ticketRemaining,
							t.ticket_isAvailable as ticketIsAvailable,
							t.ticket_update as ticketUpdate		
				from 		ticket t
				join 		event e
				on 			e.event_id=t.event_id
				where		e.event_id=?
				""".trim();
		
		try {

			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TicketDtos.class),eventId);
		} catch (Exception e) {
			logger.info("findTicketDtosById搜出錯誤",e.getMessage());
			throw new RuntimeException("findTicketDtosById搜出錯誤");
		}
	}




	
	//先加入event的table
	@Override
	public int addEventDto(EventDetailDto dto,Integer hostId) {
		String sql="""
				insert into event(	event_performer ,event_name		,event_description,
							 		event_date		,event_time		,event_location,
							 		event_type		,event_status	,host_id)
							values( ?, ?, ?, ?, ?, ?, ?, ?, ?)		
				""".trim();
		
		KeyHolder keyHolder=new GeneratedKeyHolder();
		try {	System.out.println(dto);
				jdbcTemplate.update(connection->{
					PreparedStatement ps =connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
					ps.setString(1,dto.getEventPerformer());
					ps.setString(2,dto.getEventName());
					ps.setString(3,dto.getEventDescription());
					ps.setString(4,dto.getEventDate().toString());
					ps.setString(5,dto.getEventTime().toString());
					ps.setString(6,dto.getEventLocation());
					ps.setString(7,dto.getEventType());
					ps.setString(8,dto.getEventStatus());
					ps.setInt(9, hostId);
						return ps;		
				},keyHolder);
				
				return keyHolder.getKey().intValue();
			
		} catch (Exception e) {
				logger.info("EventDto新增錯誤",e.getMessage());
				throw new RuntimeException("EventDto新增錯誤"+e.getMessage());
		}

		
		
	}




	
	
	
	@Override
	public void addSalesStatus(Integer eventId) {
		String sql="""
				insert into sales(event_id,sales_status)
				values(?,'未開賣')
				
				""".trim();
		try {
			jdbcTemplate.update(sql,eventId);			
		} catch (Exception e) {
			logger.info("addSalesStatus 添加錯誤"+e.getMessage());
			throw new RuntimeException("addSalesStatus 添加錯誤"+e.getMessage());
		
		}
		
	}

	
	
	
	
	
	
}
