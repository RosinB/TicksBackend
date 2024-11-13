package com.example.demo.model.dto.sales;
import lombok.Data;

@Data
public class SalesDto {

	//銷售紀錄Id和活動id一樣
	private Integer salesId;

	
	//剩餘的票
	private Integer salesRemaining;

	//活動狀態 
	private String salesStatus;
	
	//活動id
	private Integer eventId;
	
	
	//活動總票數
	private Integer eventTotalTickets;
	
	//銷售票數
	private Integer salesSold;
	
}
