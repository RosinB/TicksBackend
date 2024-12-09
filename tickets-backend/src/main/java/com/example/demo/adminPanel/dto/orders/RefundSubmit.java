package com.example.demo.adminPanel.dto.orders;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RefundSubmit {
    public Integer refundId;
	public Integer eventId;
	public Integer userId;
	public Integer orderId;
	public String refundStatus;
	public LocalDateTime refundTime;
	public LocalDateTime orderDateTime;
	public String refundTitle;
	public String refundReason;
	
	
	
}
