package com.example.demo.model.dto.sales;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTicketSalesDto {

		private Integer eventId;
		
		private String section;
		
		private Integer quantity;

		private String userName;
		
		
	
}