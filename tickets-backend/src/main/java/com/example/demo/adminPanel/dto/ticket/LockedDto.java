package com.example.demo.adminPanel.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LockedDto {

	private Integer eventId;
	private String ticketName;
	private Boolean ticketIsAvailable;
	
	
}
