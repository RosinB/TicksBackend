package com.example.demo.adminPanel.repository.event;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.LockedDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeTicketDto;
import com.example.demo.adminPanel.dto.ticket.StatusOnSaleDto;
import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.util.DatabaseUtils;

@Repository
public class AdminEventJDBCImpl implements AdminEventJDBC{
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final class SQL{
		static final String FIND_EVENT_ALL_DTO="""
				select event_id as eventId,
				   event_name as eventName,
				   event_performer as eventPerformer,
				   event_date as eventDate,
				   event_status as eventStatus
			from event
			""".trim();
		static final String FIND_DETAIL_BY_EVENTID="""
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
			    p.pic_event_ticket 		AS picEventTicket,
			    p.pic_event_list 		AS picEventList,
			    p.pic_event_section		AS picEventSection
			    
FROM			event e
JOIN			host h
ON				e.host_id = h.host_id
JOIN			pic p
ON				e.event_id = p.event_id 
WHERE			e.event_id = ?
															
""".trim();
		static final String FIND_TICKETDTO_BY_ID="""
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
		static final String ADD_EVENTDTO="""
				insert into event(	event_performer ,event_name		,event_description,
		 		event_date		,event_time		,event_location,
		 		event_type		,event_status	,host_id)
		values( ?, ?, ?, ?, ?, ?, ?, ?, ?)		
""".trim();
		static final String ADD_SALES_STATUS="""
				insert into sales(event_id,sales_status)
				values(?,'未開賣')
				
				""".trim();
		static final String UPDATE_EVENTDTO="""
				update  event
				set  	event_performer		=?,
						event_name			=?,
						event_description	=?,
						event_date			=?,
						event_time			=?,
						event_location		=?,
						event_type			=?,
						event_status		=?,
						host_id				=?
				where 	event_id			=?
			""".trim();
		static final String FIND_STATUS_ONSALE="""
				select 		  
				   e.event_id		 as eventId,
				   e.event_name 	 as eventName,
				   e.event_salesdate as eventSalesDate,
				   e.event_salestime as eventSalesTime,
				   e.event_date		 as eventDate,
				   e.event_time	     as eventTime,
				   e.event_status    as eventStatus
	
		
	from 			event e

	where  			e.event_status='即將舉辦'
			
	""".trim();
		static final String FIND_REALTIME_TICKET_BY_ID="""
				select  ticket_price as ticketPrice,
				ticket_name as ticketName,
				ticket_quantity as ticketQuantity,
				ticket_isAvailable as ticketIsAvailable
		from ticket
		where event_id=?
		""".trim();
		static final String UPDATE_STATUS="""
				update ticket
				set    ticket_isAvailable=?
				where  event_id=? and ticket_name=?
			""".trim();
	
	}
	private static final RowMapper<GetEventAllDto> AllEventMapper=new BeanPropertyRowMapper<>(GetEventAllDto.class);
	private static final RowMapper<EventDetailDto> detailMapper = new BeanPropertyRowMapper<>(EventDetailDto.class);
	private static final RowMapper<TicketDtos> ticketMapper = new BeanPropertyRowMapper<>(TicketDtos.class);
	private static final RowMapper<StatusOnSaleDto> statusMapper = new BeanPropertyRowMapper<>(StatusOnSaleDto.class);
	private static final RowMapper<RealTimeTicketDto> realTimeMapper=new BeanPropertyRowMapper<>(RealTimeTicketDto.class);
	
	@Override
	public List<GetEventAllDto> findEventAllDto() {
		
		return DatabaseUtils.executeQuery(
							"findEventAllDto", 
							()->jdbcTemplate.query(SQL.FIND_EVENT_ALL_DTO, AllEventMapper), 
							"找尋所有活動失敗");

	}

	
	//透過演唱會ID茶演唱會資料
	@Override
	public EventDetailDto findEventDetailById(Integer eventId) {
		
		return DatabaseUtils.executeQuery("findEventDetailById",
							()->jdbcTemplate.queryForObject(SQL.FIND_DETAIL_BY_EVENTID, detailMapper,eventId), 
							String.format("演唱會詳細資料查詢失敗->eventId:%d", eventId));

	}

	//透過演唱會ID查ticketDtos
	@Override
	public List<TicketDtos> findTicketDtosById(Integer eventId) {
		return DatabaseUtils.executeQuery(
							"findTicketDtosById",
							()->jdbcTemplate.query(SQL.FIND_TICKETDTO_BY_ID, ticketMapper,eventId), 
							String.format("查詢TicketDTo錯誤->eventID:%d", eventId));
				
	}

	//先加入event的table
	@Override
	public int addEventDto(EventDetailDto dto,Integer hostId) {
		
		KeyHolder keyHolder=new GeneratedKeyHolder();
		
		return DatabaseUtils.executeQuery(
							"addEventDto",
							()->{
								jdbcTemplate.update(connection->{
								PreparedStatement ps =connection.prepareStatement(SQL.ADD_EVENTDTO,Statement.RETURN_GENERATED_KEYS);
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
							}, 
							String.format("添加eventDto失敗"));
	
	}

	//增加銷售狀態
	@Override
	public void addSalesStatus(Integer eventId) {
		DatabaseUtils.executeUpdate(
					"addSalesStatus",
					()->jdbcTemplate.update(SQL.ADD_SALES_STATUS,eventId),
					String.format("SalesStatus新增失敗->eventID:%d", eventId));
		
	}
	
	//更新eventTable
	@Override
	public void updateEventDto(EventDetailDto dto,Integer hostId,Integer eventId) {
		
		DatabaseUtils.executeUpdate(
					"updateEventDto", 	
					()->jdbcTemplate.update(SQL.UPDATE_EVENTDTO,
										  dto.getEventPerformer(),dto.getEventName(),dto.getEventDescription(),
										  dto.getEventDate(),dto.getEventTime(),dto.getEventLocation(),
										  dto.getEventType(),dto.getEventStatus(),hostId,eventId),
					String.format("更新活動失敗 ->eventId:%d",eventId ));
			
		}
	
	@Override
	public List<StatusOnSaleDto> findStatusOnSale() {
	
		return DatabaseUtils.executeQuery(
							"findStatusOnSale", 
							()->jdbcTemplate.query(SQL.FIND_STATUS_ONSALE, statusMapper),
							"查詢熱賣中狀態失敗");
	}

	@Override
	public List<RealTimeTicketDto> findRealTimeTicketByEventId(Integer eventId) {
		
		return DatabaseUtils.executeQuery(
							"findRealTimeTicketByEventId",
							()->jdbcTemplate.query(SQL.FIND_REALTIME_TICKET_BY_ID, realTimeMapper,eventId), 
							String.format("查詢實時票卷失敗->eventId:%d", eventId));
		
	}

	@Override
	public void updateStatus(LockedDto lock) {
		
			DatabaseUtils.executeUpdate(
						"updateStatus",
						()->jdbcTemplate.update(SQL.UPDATE_STATUS,lock.getTicketIsAvailable(),lock.getEventId(),lock.getTicketName()), 
						"更新狀態失敗");
		
	}
}
