package com.example.demo.model.dto.sales;
import lombok.Data;

@Data
public class SalesDto {

	
	private Integer salesId;

	private Integer salesRemaining;

	private String salesStatus;
	
	private Integer eventId;
	
	private Integer eventTotalTickets;
	
}
