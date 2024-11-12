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

@Repository
@Qualifier("eventJDBC")

public class EventRespositoryJdbcImpl implements EventRespositoryJdbc {
	private static final Logger logger = LoggerFactory.getLogger(EventRespositoryJdbcImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	
	
	
	
	
	@Override
	public List<EventPicDto> findAllEventPics() {
	    String sql = """
	        select p.pic_event_ticket as eventTicketPic,
	               p.pic_event_list as eventTicketList,
	               e.event_date as eventDate,
	               p.event_name as eventName
	        from pic p
	        join event e
	        on p.event_name = e.event_name
	    """.trim();
	    
	    
	    return jdbcTemplate.query(sql, (rs, rowNum) -> 
	    {
	        EventPicDto eventPicDto = new EventPicDto();
	        eventPicDto.setEventTicketPic(rs.getString("eventTicketPic"));
	        eventPicDto.setEventTicketList(rs.getString("eventTicketList"));
	        eventPicDto.setEventDate(rs.getObject("eventDate", LocalDate.class)); // 手動映射 LocalDate
	        eventPicDto.setEventName(rs.getString("eventName"));
	        return eventPicDto;  //這一行會自己加到List query是回傳一個list
	    });
	    
	}






	@Override
	public Optional<EventDto> findEventDetail(String eventName) {
		String sql = """
				SELECT
				    e.event_id AS eventId,
				    e.event_performer AS eventPerformer,
				    e.event_name AS eventName,
				    e.event_location AS eventLocation,
				    e.event_description AS eventDescription,
				    e.event_date AS eventDate,
				    e.event_time AS eventTime,
				    e.event_price AS eventPrice,
				    e.host_id AS hostId,
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
				    e.event_name = p.event_name -- 連接 pic 表和 event 表
				WHERE
				    e.event_name = ?;
								""".trim();
		try {
			EventDto eventDto = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EventDto.class),
					eventName);
			return Optional.of(eventDto);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		return Optional.empty();
	}

}
