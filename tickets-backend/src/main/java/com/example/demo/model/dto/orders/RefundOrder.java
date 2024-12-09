package com.example.demo.model.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RefundOrder {
	public Integer orderId;
	public String userName;
	public String refundTitle;
	public String refundReason;
	
	
}
