package com.example.demo.model.dto.pic;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PicDto {
	
	private Integer picId;


//	private Integer eventId;
	
	private String	picEventTicket ;
	
	private String picEventList;
	
	
	private String picIndex;
}
