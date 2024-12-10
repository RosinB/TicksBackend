package com.example.demo.controller;


import java.util.Map;
import java.util.UUID;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.adminPanel.service.common.TrafficRecordService;
import com.example.demo.common.config.RabbitMQConfig;
import com.example.demo.common.exception.UserIsNotVerifiedException;
import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.model.dto.ticket.SeatStatusDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;
import com.example.demo.service.order.OrderService;
import com.example.demo.service.sales.SalesService;
import com.example.demo.util.ApiResponse;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.ConstantList;
import com.example.demo.util.RedisService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("sales")
@RequiredArgsConstructor
@Slf4j
public class SalesController {


	private final SalesService salesService;
	private final OrderService orderService;
	private final RabbitTemplate rabbitTemplate;
	private final RedisService redisService;
	private final TrafficRecordService trafficService;
//=======================處理售票狀況============================== 這在tiecketsales
	@PostMapping("/goticket/area/buy")

	public ResponseEntity<ApiResponse<Object>> postBuyTicket(@RequestBody PostTicketSalesDto data,
			HttpServletRequest request) {
			// 生成唯一請求 ID
			String requestId = UUID.randomUUID().toString();
			data.setRequestId(requestId);
			
			String captcha=redisService.get(CacheKeys.util.CAPTCHA_PREFIX+data.getUserName(), String.class);

			if (ConstantList.CAPTCHA) {
			    // 只在啟用驗證碼時進行驗證
			    if (!captcha.equals(data.getUserCaptcha())) {
			        return handleCaptchaError(data.getUserName());
			    }
			}
			TrafficDto trafficData = trafficService.createTrafficDto(data,request,requestId);
			 // 頻率檢查
	        Integer frequency = trafficData.getRequestFrequency();
	        if (frequency > 100) {
	        	 return handleTooManyRequests(data.getUserName(), frequency);
	        }
			// 發送購票請求到 RabbitMQ
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.TICKET_ROUTING_KEY, 
					data);
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.TRAFFIC_ROUTING_KEY, 
					trafficData);

			return ResponseEntity.ok(ApiResponse.success("購票請求已提交，正在處理", requestId));
		

	}

	@PostMapping("/goticket/area/buy/seat")
	public ResponseEntity<ApiResponse<Object>> postBuyTicketwithSeat(@RequestBody PostTicketSalesDto data) {

			// 生成唯一請求 ID
			String requestId = UUID.randomUUID().toString();
			data.setRequestId(requestId);
			salesService.buyTicketWithSeat(data);

			return ResponseEntity.ok(ApiResponse.success("購票請求已提交，正在處理", requestId));
	

	}

//========================演唱會requestId查詢==============================================
	@GetMapping("/goticket/area/status/{requestId}")
	public ResponseEntity<ApiResponse<Object>> getCheckTicketStatus(@PathVariable("requestId") String requestId) {
		try {
			// 調用通用邏輯獲取狀態
			Map<String, Object> statusResponse = orderService.getTicketStatus(requestId);

			// 根據狀態返回結果
			return ResponseEntity.ok(ApiResponse.success("查詢成功", statusResponse));
		} catch (Exception e) {
			log.error("查詢訂單狀態失敗，RequestID: {}, 錯誤: {}", requestId, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(500, "查詢訂單失敗，請稍後再試！", null));
		}
	}

//========================付款後訂單更新=============================================
	@PostMapping("/goticket/pay/{orderId}")
	public ResponseEntity<ApiResponse<Object>> postUpdateOrderStatus(@PathVariable("orderId") Integer orderId) {
		orderService.updateOrderStatus(orderId);
		return ResponseEntity.ok(ApiResponse.success("傳達成功", orderId));
	}

//========================不付錢自己取消訂單============================================
	@PostMapping("/goticket/pay/cancel/{orderId}")
	public ResponseEntity<ApiResponse<Object>> postCancelOrder(@PathVariable("orderId") Integer orderId) {
		orderService.cancelOrder(orderId);
		return ResponseEntity.ok(ApiResponse.success("傳達成功", orderId));
	}

//========================演唱會訂單摘要==============================================
	// 付款那個
	@GetMapping("/goticket/orders")
	public ResponseEntity<ApiResponse<Object>> getOrders(@RequestParam("orderId") Integer orderId,
			@RequestParam("userName") String userName, @RequestParam("requestId") String requestId) {
		OrderAstractDto dto = orderService.getOrderAbstract(orderId, userName, requestId);


		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	@GetMapping("/goticket/asborders")
	public ResponseEntity<ApiResponse<Object>> getAsbOrders(@RequestParam("orderId") Integer orderId,
			@RequestParam("userName") String userName) {
		OrderAstractDto dto = orderService.getOrderAbstract2(orderId, userName);


		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}



	// 獲得演唱會區域價錢 這是在ticketSection那頁
	@GetMapping("/goticket/area")
	public ResponseEntity<ApiResponse<Object>> getTicketSection(@RequestParam("userName") String userName,
			@RequestParam("eventId") Integer eventId) {

		try {
			TicketSectionDto ticketSectionDto = salesService.getTicketSection(eventId, userName);
			return ResponseEntity.ok(ApiResponse.success("票價區位獲取成功", ticketSectionDto));

		} catch (UserIsNotVerifiedException e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "使用者沒有認證", "使用者沒有認證"));
		}

	}

//	獲得演唱會座位資訊 
	@GetMapping("/goticket/area/seat")
	public ResponseEntity<ApiResponse<Object>> getSeatStatus(@RequestParam("eventId") Integer eventId,
			@RequestParam("section") String section) {

		SeatStatusDto dto = salesService.checkSeatStatus(eventId, section);

		return ResponseEntity.ok(ApiResponse.success("傳達成功", dto));
	}

	
	
	
	
	
	 // 錯誤響應處理方法
    private ResponseEntity<ApiResponse<Object>> handleCaptchaError(String userName) {
        log.warn("使用者驗證失敗:{}", userName);
        return ResponseEntity.status(400)
                .body(ApiResponse.error(400, "驗證失敗", null));
    }

    private ResponseEntity<ApiResponse<Object>> handleTooManyRequests(String userName, Integer frequency) {
        log.warn("使用者請求過多:{}, 頻率:{}", userName, frequency);
        return ResponseEntity.status(429)
                .body(ApiResponse.error(429, "請求過於頻繁，請稍後再試", null));
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
