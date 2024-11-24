package com.example.demo.model.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	
	
	private Integer orderId;
	
	private String orderStatus;
	

}
