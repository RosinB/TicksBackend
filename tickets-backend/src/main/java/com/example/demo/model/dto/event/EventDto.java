package com.example.demo.model.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
	
	
	private Integer eventId;
	
	private String eventPerformer;
	
	private String eventName;

	private String eventDescription;

	private LocalDateTime eventDate;

	private BigDecimal eventPrice;

	private String eventLocation;

	private String eventType;

	private Integer eventTotalTickets;

	private String eventStatus;

	private Integer hostId;
	
	private String hostName;
	

}
