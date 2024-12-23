package com.example.demo.model.dto.ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketSectionDto {
	//演唱會id
	private Integer eventId;
	
	//演唱會歌手
	private String eventPerformer;
	
	//演唱會名字
	private String eventName;
	
	//主辦名字
	private String hostName;
	
	//舉辦日期
	private LocalDate eventDate;
	
	//舉辦時間
	private LocalTime eventTime;
	
	//活動地點
	private String eventLoaction;
	
	
	//列表圖片
	private String ticketPicList;
	
	//座位圖
	private String ticketPicSection;
	
	
	//票價訊息
	private List<TicketDto> ticketDto;
	
	

}
