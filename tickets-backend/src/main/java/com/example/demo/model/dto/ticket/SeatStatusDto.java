package com.example.demo.model.dto.ticket;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SeatStatusDto {

		private Integer quantity;
		private Map<Integer,String> seatStatus;

	
}
