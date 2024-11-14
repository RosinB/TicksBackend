package com.example.demo.model.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketSectionQuantityDto {

	private String eventName;
	
	private Integer quantity;
	
}
