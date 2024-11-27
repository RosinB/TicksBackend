package com.example.demo.adminPanel.dto.ticket;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusOnSaleDto {

	
	private Integer eventId;
	private String eventName;
	private LocalDate eventSalesDate;
	private LocalTime eventSalesTime;
	private LocalDate eventDate;
	private LocalTime eventTime;
	private String eventStatus;
	
	private String salesStatus;
	
	
	
	
}
