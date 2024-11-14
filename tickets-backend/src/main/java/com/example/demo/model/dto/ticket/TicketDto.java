package com.example.demo.model.dto.ticket;

import lombok.Data;

@Data
public class TicketDto {

	private Integer ticketId;
		
	private Integer ticketPrice;
	
	private String ticketName;
	
	private Integer ticketQuantity;
	
	private Integer ticketRemaining;
	
	private Boolean ticketIsAvailable =true;
	
	
	
}
