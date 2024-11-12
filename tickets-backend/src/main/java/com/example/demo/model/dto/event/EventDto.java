package com.example.demo.model.dto.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
	
	//演唱會id
	private Integer eventId;

	//演唱會歌手
	private String eventPerformer;
	
	//演唱會名字
	private String eventName;

	//演唱會描述
	private String eventDescription;

	//演唱會日期
	private LocalDate eventDate;

	//演唱會時間
	private LocalTime evenTime;
	
	//演唱會票價
	private BigDecimal eventPrice;

	//演唱會地點
	private String eventLocation;

	//演唱會網站圖片
	private String eventTicketPic;
	
	//演唱會種類
	//private String eventType;

	//演唱會人數
	//private Integer eventTotalTickets;

	//演唱會狀態
	//private String eventStatus;

	
	//主辦id
	private Integer hostId;
	//主辦名字
	private String hostName;
	

}
