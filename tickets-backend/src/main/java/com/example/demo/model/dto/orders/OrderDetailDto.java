package com.example.demo.model.dto.orders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
////	使用者名字
//	private String userName;
////	使用者電話
//	private String userPhone;
////  使用者電郵
//	private String userEmail;

	
//票價
	private Integer ticketPrice;
	
	
//=====================主辦名字================================
	private String hostName;
	
	

	
	
	
	

	
	
}
