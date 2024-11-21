package com.example.demo.adminPanel.dto.ticket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDtos {

		
	private Integer ticketPrice;
	
	private String ticketSection;
	
	private Integer ticketQuantity;
	
	private Integer ticketRemaining;
	
	private Boolean ticketIsAvailable =true;
	
	private LocalDateTime ticketUpdate;
	
	
}
