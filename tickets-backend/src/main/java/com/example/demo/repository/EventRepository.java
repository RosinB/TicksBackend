package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.event.Event;
import com.example.demo.model.dto.event.*;

@Repository
@Qualifier("eventJPA")
public interface EventRepository extends JpaRepository<Event, Integer>{
	
//	@Query("select e from Event e ")
//	List<EventDto> findAllEvent();
	

}