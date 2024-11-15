package com.example.demo.model.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckSectionStatusDto {
	
	//演唱會id
	private Integer eventId;
	
	//演唱會區域名字
	private String section;
	
	//演唱會剩票
	private Integer ticketRemaining; 
	
	//演唱會售票狀態
	private Boolean ticketIsAvailable;
}
