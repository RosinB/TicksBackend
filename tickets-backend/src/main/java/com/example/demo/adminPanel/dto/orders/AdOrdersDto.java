package com.example.demo.adminPanel.dto.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	
	private List<Integer> poolNumber = new ArrayList<>();
	
	private List<String> seats = new ArrayList<>();
	
	public void addPoolNumber(Integer poolNumber) {
		if(poolNumber!=null) {
			this.poolNumber.add(poolNumber);
			  int row = (poolNumber - 1) / 25 + 1;
	            int seatNumber = (poolNumber - 1) % 25 + 1;
	            this.seats.add("第" + row + "排 第" + seatNumber + "號");
		}	
	}
    // 獲取座位描述的字串，用逗號分隔
	public String getSeatsDisplay() {
	        return String.join(", ", seats);
	    }
	
	
	
	
}
