package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.event.Event;
import com.example.demo.model.dto.event.*;
import com.example.demo.model.dto.pic.PicDto;

@Repository
@Qualifier("eventJPA")
public interface EventRepository extends JpaRepository<Event, Integer>{
	

	
	Integer findEventIdByEventName(String Name);

	@Query("select e.eventName from Event e where e.eventId= :eventId")
	String findEventNamebyEventId(@Param("eventId") Integer eventId);
	
	

	
	
}
