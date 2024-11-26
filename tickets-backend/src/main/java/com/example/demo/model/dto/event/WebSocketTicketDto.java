package com.example.demo.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketTicketDto {
	  private  Integer eventId;
	  private  String  section;
}
