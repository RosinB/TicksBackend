package com.example.demo.adminPanel.dto.orders;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdOrdersDto {

	private Integer orderId;
	
	
	private String eventName;
	
	private String eventPerformer;
	
	private String userName;
	
	private Integer orderQuantity;
	
	private String orderSection;
	
	private String orderStatus;
	
	private LocalDateTime orderUpdate;
	
	
}
