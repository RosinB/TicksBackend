package com.example.demo.controller;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.UserIsNotVerifiedException;
import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.repository.sales.SalesRespositoryJdbcImpl;
import com.example.demo.service.order.OrderService;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.ApiResponse;


@RestController
@RequestMapping("sales")
public class SalesController {

	private final static Logger logger = LoggerFactory.getLogger(SalesRespositoryJdbcImpl.class);

	@Autowired
	SalesService salesService;

	@Autowired
	OrderService orderService;

//=======================處理售票狀況============================== 這在tiecketsales
	@PostMapping("/goticket/area/buy")

	public ResponseEntity<ApiResponse<Object>> postBuyTicket(@RequestBody PostTicketSalesDto data) {		

		int orderId = salesService.buyTicket(data);
		
		return ResponseEntity.ok(ApiResponse.success("購票成功", orderId));

	}

//========================演唱會訂單摘要==============================================
	@GetMapping("/goticket/orders")
	public ResponseEntity<ApiResponse<Object>> getOrders(@RequestParam("orderId") Integer orderId,
			@RequestParam("userName") String userName) {

		OrderAstractDto dto = orderService.getOrderAbstract(orderId, userName);
		logger.info("使用者簡易訂單" + dto);

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	// 獲取演唱會的銷售資訊
	@GetMapping("/goticket/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getAllTickets(@PathVariable("eventId") Integer eventId) {
		SalesDto salesDto = salesService.getTickets(eventId);

		return ResponseEntity.ok(ApiResponse.success("查詢成功", salesDto));
	}

	
	
	
	// 獲得演唱會區域價錢 這是在ticketSection那頁
	@GetMapping("/goticket/area")
	public ResponseEntity<ApiResponse<Object>> getTicketSection(@RequestParam("userName") String userName,
			@RequestParam("eventId") Integer eventId) {

		try {
			TicketSectionDto ticketSectionDto = salesService.getTicketSection(eventId, userName);
			return ResponseEntity.ok(ApiResponse.success("票價區位獲取成功", ticketSectionDto));

		} catch (UserIsNotVerifiedException e) {
			logger.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "使用者沒有認證", "使用者沒有認證"));
		}

	}

	
	
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handSalesRunTimeException(RuntimeException e) {
		logger.info("Sales有RuntimeException:" + e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
	}

}
