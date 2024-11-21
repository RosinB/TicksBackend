package com.example.demo.adminPanel.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.demo.adminPanel.dto.ticket.TicketDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailDto {

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
		private LocalTime eventTime;
		
		//演唱會地點
		private String eventLocation;

		private String eventType;
		
		private String eventStatus;
//	================================	
		//主辦名字
		private String hostName;
//=====================================		
		private String salesStatus;
		
//=====================================
		
		private List<TicketDtos> ticketDtos;
//=======================================
		private String picEventList;
		
		private String picEventSection;
		
		private String picEventTicket;
		
		
//========================================
		
		
		
	
	
	
}
