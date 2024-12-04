package com.example.demo.repository.event;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventPicDto;
import com.example.demo.model.dto.pic.PicDto;
import com.example.demo.util.DatabaseUtils;

@Repository
@Qualifier("eventJDBC")

public class EventRespositoryJdbcImpl implements EventRespositoryJdbc {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private final RowMapper<EventPicDto> eventPicMapper = new BeanPropertyRowMapper<>(EventPicDto.class);
	private final RowMapper<PicDto> picMapper = new BeanPropertyRowMapper<>(PicDto.class);
	private final RowMapper<EventDto> eventMapper = new BeanPropertyRowMapper<>(EventDto.class);
	
	public static final class SQL{
		static final String FIND_ALL_EVENT_PICS="""
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
		static final String FIND_PIC_BY_EVENTID="""
				select 	p.pic_id as picId,
				p.pic_event_ticket as picEventTicket,
				p.pic_event_list as picEventList,
				p.pic_event_section as picEventSection
		from pic p
		where p.event_id=?

		
		""".trim();
		static final String FIND_EVENT_DETAIL_BY_EVENTID="""
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
		static final String FIND_EVENTNAME_BY_EVENTID="""
				select event_name
				from event
				where event_id=?
				
				
				""".trim();
		static final String FIND_QUANTITY_BY_EVENTID_AND_SECTION="""
				select ticket_quantity 
				from ticket
				where event_id=? and ticket_name=?
				""".trim();
		static final String CHECK_SEATS_STATUS="""
				select pool_number, pool_status
				from pool
				where event_id=? and event_section=?			
			""".trim();
	};
	
	
	
	
	
	@Override
	public List<EventPicDto> findAllEventPics() {
	    
		return DatabaseUtils.executeQuery(
							"findAllEventPics", 
							()->jdbcTemplate.query(SQL.FIND_ALL_EVENT_PICS, eventPicMapper), 
							"找尋所有活動照片失敗");
		    
	}

	@Override
	public PicDto findPicByEventId(Integer eventId) {
		
		return DatabaseUtils.executeQuery(
							"findPicByEventId", 
							()->jdbcTemplate.queryForObject(SQL.FIND_PIC_BY_EVENTID, picMapper,eventId), 
							String.format("透過活動ID:%d->搜尋失敗", eventId));

	}

	@Override
	public Optional<EventDto> findEventDetailByEventId(Integer eventId) {
		
		return Optional.ofNullable(
					DatabaseUtils.executeQuery(
							"findEventDetailByEventId", 
							()->jdbcTemplate.queryForObject(SQL.FIND_EVENT_DETAIL_BY_EVENTID, eventMapper,eventId), 
							String.format("搜尋演唱會詳細資料失敗，EventId:%d", eventId))
			);
				
	}


	@Override
	public String findEventNameByEventId(Integer eventId) {
		
		
		return DatabaseUtils.executeQuery(
							"findEventNameByEventId", 
							()->jdbcTemplate.queryForObject(SQL.FIND_EVENTNAME_BY_EVENTID, String.class,eventId), 
							String.format("搜尋演唱會名字失敗 EventId:%d", eventId));
		
	}

//找總票數
	@Override
	public Integer findQuantityByEventIdAndSection(Integer eventId, String section) {
	
		return DatabaseUtils.executeQuery(
							"findQuantityByEventIdAndSection", 
							()->jdbcTemplate.queryForObject(SQL.FIND_QUANTITY_BY_EVENTID_AND_SECTION, Integer.class,eventId,section), 
							String.format("找尋票卷數量失敗，eventId:%d和section:%s",eventId,section));
			
	}


	
	@Override
	public Map<Integer, String> checkSeatStatus(Integer eventId, String section) {
		
		  
		 List<Map<String, Object>> seatStatusList=DatabaseUtils.executeQuery(
				 												"checkSeatStatus",
				 												()->jdbcTemplate.queryForList(SQL.CHECK_SEATS_STATUS, eventId, section),
				 												String.format("找尋座位狀態失敗->eventId:%d section:%s", eventId,section));
		 
		 Map<Integer, String> seatStatusMap = new HashMap<>();

		    for (Map<String, Object> seat : seatStatusList) {
		        Integer poolNumber = (Integer) seat.get("pool_number");
		        String poolStatus = (String) seat.get("pool_status");

		        seatStatusMap.put(poolNumber, poolStatus);
		    }

		    
		    return seatStatusMap;
		
	}

	
	
	
	
	
}
