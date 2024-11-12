package com.example.demo.service.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.EventMapper;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.event.EventRespositoryJdbc;

@Service
public class EventServiceImpl implements EventService {

	private final static Logger logger =LoggerFactory.getLogger(EventServiceImpl.class);
	
	
	@Autowired
	@Qualifier("eventJPA")
	EventRepository eventRepository;

	@Autowired 
	EventMapper eventMapper;
	
	@Autowired
	@Qualifier("eventJDBC")
	EventRespositoryJdbc eventRespositoryJdbc;
	
	@Override
	public List<EventDto> findAllEvent() {
		
		return eventRepository.findAll().stream()
							  .map(eventMapper::toDto)
							  .collect(Collectors.toList());
	}


	
	@Override
	public Optional<EventDto> getEventDetailsByName(String eventName) {
		
		logger.info("在service層，找到"+eventRespositoryJdbc.findEventDetail(eventName));
		return eventRespositoryJdbc.findEventDetail(eventName);

	}
	
	
	
	
	
}
