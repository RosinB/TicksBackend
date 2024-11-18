package com.example.demo.repository.event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
	               e.event_name as eventName
	        from pic p
	        join event e
	        on p.event_id = e.event_id
	    """.trim();
	    
	    
	    return jdbcTemplate.query(sql, (rs, rowNum) -> 
	    {
	        EventPicDto eventPicDto = new EventPicDto();
	        eventPicDto.setEventId(rs.getInt("eventId"));
	        eventPicDto.setEventTicketPic(rs.getString("eventTicketPic"));
	        eventPicDto.setEventTicketList(rs.getString("eventTicketList"));
	        eventPicDto.setEventDate(rs.getObject("eventDate", LocalDate.class)); // 手動映射 LocalDate
	        eventPicDto.setEventName(rs.getString("eventName"));
	        return eventPicDto;  //這一行會自己加到List query是回傳一個list
	    });
	    
	}


	@Override
	public PicDto findPicByEventId(Integer eventId) {
		String sql="""
				select 	p.pic_id as picId,
						p.pic_event_ticket as picEventTicket,
						p.pic_event_list as picEventList,
						p.pic_index as picIndex
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
				    h.host_name AS hostName,
				    p.pic_event_ticket AS eventTicketPic
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
			throw new RuntimeException("演唱會資訊找不到"+eventId,e);
		}
	}

}
