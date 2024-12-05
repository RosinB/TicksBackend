package com.example.demo.adminPanel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.event.GetEventAllDto;
import com.example.demo.adminPanel.dto.ticket.LockedDto;
import com.example.demo.adminPanel.dto.ticket.RealTimeDto;
import com.example.demo.adminPanel.dto.ticket.StatusOnSaleDto;

import com.example.demo.adminPanel.service.event.AdminEventService;
import com.example.demo.adminPanel.service.ticket.AdTicketService;
import com.example.demo.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/event")
public class AdminEventController {

	private final AdminEventService adminEventService;
	private final AdTicketService adTicketService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getAllEvents() {

		List<GetEventAllDto> dto = adminEventService.getAllEvent();

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getEventById(@PathVariable("eventId") Integer eventId) {

		EventDetailDto dto = adminEventService.getEventById(eventId);

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<Object>> postAddEvent(@RequestBody EventDetailDto dto) {

		adminEventService.addEvent(dto);

		return ResponseEntity.ok(ApiResponse.success("傳送成功", "傳達成功"));
	}

	// 更新資料
	@PostMapping("/update")
	public ResponseEntity<ApiResponse<Object>> postUpdateEvent(@RequestBody EventDetailDto dto) {

		EventDetailDto dtos = adminEventService.updateEvent(dto);
		return ResponseEntity.ok(ApiResponse.success("傳達成功", dtos));
	}

	@GetMapping("/onsale")
	public ResponseEntity<ApiResponse<Object>> getEventOnSale() {

		List<StatusOnSaleDto> dto = adminEventService.getStatusOnSale();

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	@GetMapping("/onsale/{eventId}")
	public ResponseEntity<ApiResponse<Object>> getRealTimeTicket(@PathVariable("eventId") Integer eventId) {

		RealTimeDto dto = adminEventService.getRealTimeDto(eventId);

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	@PostMapping("/api/lock")
	public ResponseEntity<ApiResponse<Object>> postLockTicket(@RequestBody LockedDto lock) {

		adminEventService.LockTicket(lock);
		return ResponseEntity.ok(ApiResponse.success("傳達成功", lock));
	}

	@PostMapping("/api/balance")
	ResponseEntity<ApiResponse<Object>> postBalanceTicket(@RequestParam("eventId") Integer eventId,
			@RequestParam("section") String section) {

		adTicketService.blanceTicket(eventId, section);

		return ResponseEntity.ok(ApiResponse.success("更新成功", "ok"));
	}

	@PostMapping("/api/clear")
	ResponseEntity<ApiResponse<Object>> postclearTicket(@RequestParam("eventId") Integer eventId,
			@RequestParam("section") String section) {

		adTicketService.clearTicket(eventId, section);

		return ResponseEntity.ok(ApiResponse.success("更新成功", "ok"));
	}

}
