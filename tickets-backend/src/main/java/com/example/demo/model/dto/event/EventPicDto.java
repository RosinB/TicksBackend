package com.example.demo.model.dto.event;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EventPicDto {


	private String eventName;
	private LocalDate eventDate;
	//售票網站圖片和首頁圖片
	private String eventTicketPic;
	//購票列表的圖片
	private String eventTicketList;
	
	
	
}
