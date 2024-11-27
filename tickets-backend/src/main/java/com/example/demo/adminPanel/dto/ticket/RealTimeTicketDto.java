package com.example.demo.adminPanel.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RealTimeTicketDto {
	
	
	private Integer ticketPrice;
	
	private String ticketName;
	
	private Integer ticketQuantity;
	
	private Boolean ticketIsAvailable =true;
}
