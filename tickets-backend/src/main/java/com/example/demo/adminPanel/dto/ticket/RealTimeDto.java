package com.example.demo.adminPanel.dto.ticket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RealTimeDto {

	Integer eventId;
	String eventName;
	
	List<RealTimeTicketDto> dto;
	
	
}
