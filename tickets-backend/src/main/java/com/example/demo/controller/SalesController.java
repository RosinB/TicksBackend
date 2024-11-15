package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.sales.CheckSectionStatusDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.sales.SalesRespositoryJdbcImpl;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("sales")
public class SalesController {

	private final static Logger logger = LoggerFactory.getLogger(SalesRespositoryJdbcImpl.class);
	
	
	@Autowired
	SalesService salesService;
	//獲取演唱會的銷售資訊
	@GetMapping("/goticket/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getAllTickets(@PathVariable("eventId") Integer eventId){	
		SalesDto salesDto=salesService.getTickets(eventId);
//		System.out.println("這是salesDTo"+salesDto);

		if(Optional.of(salesDto).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "查詢失敗 Saels查詢不到", null));	
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", salesDto));
	}

	
	//處理售票狀況 這在tiecketsales
	@PostMapping("/goticket/area/buy")
	public ResponseEntity<ApiResponse<Object>> postBuyTicket(@RequestBody PostTicketSalesDto data) {
	    System.out.println("接收到的數據: " + data);

	    CheckSectionStatusDto dto=salesService.getTicketRemaining(data.getSection(), data.getEventId());
	  
	    try {
		    salesService.buyTicket(data);
		    return ResponseEntity.ok(ApiResponse.success("購票成功", dto));
		    
		} catch (RuntimeException e) {
			logger.warn("購票失敗");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "購票失敗", null));
		}
		
					

	}
	
	
	
	
	//獲得演唱會區域價錢 這是在ticketSection那頁
    @GetMapping("/goticket/area/{eventId}") 
    public ResponseEntity<ApiResponse<Object>> getTicketSection(@PathVariable("eventId") Integer eventId) {
    	TicketSectionDto ticketSectionDto=salesService.getTicketSection(eventId);
//    	System.out.println("進入到getticketsection"+ticketSectionDto);

		return ResponseEntity.ok(ApiResponse.success("票價區位獲取成功", ticketSectionDto));
		
	}
	
	
	
}
