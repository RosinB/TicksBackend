package com.example.demo.adminPanel.dto.event;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEventAllDto {

	private Integer eventId;
	
	private String eventName;
	
	private String eventPerformer;
	
	private LocalDate eventDate;
	
	private String eventStatus;
	
	
	
}
