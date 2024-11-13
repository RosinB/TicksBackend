package com.example.demo.model.dto.sales;

import lombok.Data;

@Data
public class BuyTicketDto {

	private String userName;
	private Integer eventId;
	private Integer quantity;
}
