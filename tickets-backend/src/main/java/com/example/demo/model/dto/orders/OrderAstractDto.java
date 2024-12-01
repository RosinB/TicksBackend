package com.example.demo.model.dto.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	
	   private List<Integer> poolNumbers = new ArrayList<>();  // 初始化列表
	    private List<String> seats = new ArrayList<>();        // 初始化列表
	
	    public void addPoolNumber(Integer poolNumber) {
	        if (poolNumber != null) {
	            this.poolNumbers.add(poolNumber);
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
