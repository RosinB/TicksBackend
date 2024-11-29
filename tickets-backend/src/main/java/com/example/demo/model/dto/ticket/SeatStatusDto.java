package com.example.demo.model.dto.ticket;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeatStatusDto {

		private Integer quantity;
		private Map<Integer,Boolean> seatStatus;

	
}
