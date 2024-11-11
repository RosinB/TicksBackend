package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.entity.event.Event;

@Component
public class EventMapper {

	@Autowired
	private ModelMapper modelMapper;

	public EventDto toDto(Event event) {

		return modelMapper.map(event, EventDto.class);

	}

	public Event toEntity(EventDto eventDto) {

		return modelMapper.map(eventDto, Event.class);
	}

}
