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

	public Integer eventId;
	public Integer userName;
	public Integer orderId;
	public String RefundStatus;
	public LocalDateTime RefundTime;
	public LocalDateTime OrderDateTime;
	public String RefundTitle;
	public String RefundReason;
	
	
	
}
