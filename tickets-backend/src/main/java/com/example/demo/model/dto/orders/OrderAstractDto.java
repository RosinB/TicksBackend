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
	
	private Integer poolNumber;
	
	private String seat;
	
	public void setPoolNumber(Integer poolNumber) {
	    this.poolNumber = poolNumber;
	    setSeat(); // 自动生成座位描述
	}
	
	public  void setSeat() 
	{if (poolNumber != null) {
	        int row = (poolNumber - 1) / 25 + 1; // 假設每排 20 個座位
	        int seatNumber = (poolNumber - 1) % 25 + 1;
	        this.seat = "第" + row + "排 第" + seatNumber + "號";
	        }
   }
	
}
