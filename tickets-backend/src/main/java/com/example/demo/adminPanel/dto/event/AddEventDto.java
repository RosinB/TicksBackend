package com.example.demo.adminPanel.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEventDto {
	

	private Integer eventId;
	
	private String eventPerformer;
	
	private String eventName;

	private String eventDescription;

	private LocalDate eventDate;

	private LocalTime eventTime;
	
	private String eventLocation;

	private String eventType;

	private String eventStatus;

	private Integer hostId;

}
