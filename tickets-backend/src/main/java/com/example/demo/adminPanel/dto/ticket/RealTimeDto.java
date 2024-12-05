package com.example.demo.adminPanel.dto.ticket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RealTimeDto {

	Integer eventId;
	String eventName;
	
	List<RealTimeTicketDto> dto;
	
	
}
