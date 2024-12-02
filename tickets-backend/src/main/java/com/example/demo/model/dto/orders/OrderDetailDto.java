package com.example.demo.model.dto.orders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

//==========================訂單區=============================	
//	訂單編號
	private Integer orderId;
//票卷數量
	private Integer orderQuantity;
//票卷位置
	private String orderSection;
//訂單時間
	private LocalDateTime orderDateTime;
//票卷狀態
	private String orderStatus;

//=======================演唱會區===============================
// 演唱會名字	
	private String eventName;
// 演唱會歌手名字
	private String eventPerformer;
//演唱會日期	
	private LocalDate eventDate;
//演唱會時間
	private LocalTime eventTime;
//演唱會地點 
	private String eventLocation;

//======================使用者區==============================
	private List<Integer> poolNumber =new ArrayList<>();
	private List<String>  seats =new ArrayList<>();
	
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
	

	
//票價
	private Integer ticketPrice;
	
	
//=====================主辦名字================================
	private String hostName;
	
	private String requestId;
	

	
	
	
	

	
	
}
