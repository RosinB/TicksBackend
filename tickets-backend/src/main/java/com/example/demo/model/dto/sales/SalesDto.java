package com.example.demo.model.dto.sales;
import lombok.Data;

@Data
public class SalesDto {

	//銷售紀錄Id和活動id一樣
	private Integer salesId;

	//活動狀態 
	private String salesStatus;
	
	//活動id
	private Integer eventId;
	
	
	//銷售票數
	private Integer salesSold;
	
}
