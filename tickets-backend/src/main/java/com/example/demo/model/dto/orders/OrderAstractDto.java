package com.example.demo.model.dto.orders;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAstractDto {

	private Integer orderId;
	
	private String eventName;
	
	private String userName;
	
	private String orderSection;
	
	private Integer orderPrice;
	
	private LocalDateTime  orderDateTime;
	
	private String orderStatus;
	
	
}
