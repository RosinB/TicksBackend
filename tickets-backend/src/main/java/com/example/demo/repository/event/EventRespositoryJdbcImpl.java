package com.example.demo.repository.event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;

@Repository
@Qualifier("eventJDBC")

public class EventRespositoryJdbcImpl implements EventRespositoryJdbc {
	private static final Logger logger = LoggerFactory.getLogger(EventRespositoryJdbcImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	
	
	
	
	
	@Override
	public List<EventPicDto> findAllEventPics() {
	    String sql = """
	        select p.pic_id as eventId,
	    		   p.pic_event_ticket as eventTicketPic,
	               p.pic_event_list as eventTicketList,
	               e.event_date as eventDate,
	               e.event_time as eventTime,
	               e.event_name as eventName,
	               e.event_salestime as eventSalesTime,
	               e.event_salesdate as eventSalesDate
	        from pic p
	        join event e
	        on p.event_id = e.event_id
	    """.trim();
	    
	    try {
	    	return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EventPicDto.class));

	    	
		} catch (Exception e) {
			logger.info("findAllEventPics資料查訊失敗"+e.getMessage());
			throw new RuntimeException("findAllEventPics資料查訊失敗"+e.getMessage());
		
		}
	  
	    
	}


	@Override
	public PicDto findPicByEventId(Integer eventId) {
		String sql="""
				select 	p.pic_id as picId,
						p.pic_event_ticket as picEventTicket,
						p.pic_event_list as picEventList,
						p.pic_event_section as picEventSection
				from pic p
				where p.event_id=?
	
				
				""".trim();

		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(PicDto.class),eventId);
	}
	
	
	
	





	@Override
	public Optional<EventDto> findEventDetailByEventId(Integer eventId) {
		String sql = """
				SELECT
				    e.event_id AS eventId,
				    e.event_performer AS eventPerformer,
				    e.event_name AS eventName,
				    e.event_location AS eventLocation,
				    e.event_description AS eventDescription,
				    e.event_date AS eventDate,
				    e.event_time AS eventTime,
				    e.event_salesdate AS eventSalesDate,
				    e.event_salestime AS eventSalesTime,
				    h.host_name AS hostName,
				    p.pic_event_ticket AS eventTicketPic,
				    p.pic_event_section AS picTicketSection
				FROM
				    event e
				JOIN
				    host h
				ON
				    e.host_id = h.host_id
				JOIN
				    pic p
				ON
				    e.event_id = p.event_id -- 連接 pic 表和 event 表
				WHERE
				    e.event_id = ?;
								""".trim();
		try {
			EventDto eventDto = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EventDto.class),
					eventId);
			return Optional.of(eventDto);
			
		} catch (Exception e) {
			logger.info("演唱會 findEventDetailByEventId 找不到");
			throw new RuntimeException("演唱會資訊找不到"+eventId,e);
		}
	}


	@Override
	public String findEventNameByEventId(Integer eventId) {
		
		String sql="""
				select event_name
				from event
				where event_id=?
				
				
				""".trim();
			
		try {
			return jdbcTemplate.queryForObject(sql, String.class,eventId);
			
			
		} catch (Exception e) {
			logger.info(" findEventNameByEventId找不到:"+e.getMessage());
			throw new RuntimeException(" findEventNameByEventId找不到:"+e.getMessage());
		}
		
		
		
	}

//找總票數
	@Override
	public Integer findQuantityByEventIdAndSection(Integer eventId, String section) {
		String sql="""
				select ticket_quantity 
				from ticket
				where event_id=? and ticket_name
				""".trim();
		
		try {
			return jdbcTemplate.queryForObject(sql, Integer.class);

		} catch (Exception e) {
			logger.info(" ffindQuantityByEventIdAndSection找不到:"+e.getMessage());
			throw new RuntimeException(" findQuantityByEventIdAndSection找不到:"+e.getMessage());		}
			
	}


	
	@Override
	public Map<Integer, Boolean> checkSeatStatus(Integer eventId, String section) {
		String sql="""
					select pool_number,
					select pool_status
					from pool
					where event_id=? and section=?			
				""".trim();
		
		return null;


//找座位狀態
	
	
	
	
	
	
}
