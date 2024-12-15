package com.example.demo.service.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;
import com.example.demo.model.dto.orders.RefundOrder;
import com.example.demo.repository.order.OrderRepositoryJdbc;
import com.example.demo.repository.sales.SalesRepositoryJdbc;
import com.example.demo.service.common.EmailService;
import com.example.demo.service.common.RefundService;
import com.example.demo.service.user.UserService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
	
	
	
	private final UserService userService;
	private final SalesRepositoryJdbc salesRepositoryJdbc;
	private final RedisService redisService;
	private final OrderRepositoryJdbc orderRepositoryJdbc;
	private final RefundService refundService;
	private final EmailService emailService;
	
	public Map<String, Object> getTicketStatus(String requestId) {
		
        // 檢查是否存在對應的記錄
		String redisStatus = redisService.get(CacheKeys.Order.ORDER_PREFIX+requestId,String.class);
		
	 
	    if ("FAILED".equals(redisStatus)) {
	        return Map.of(
	            "status", "錯誤",
	            "errorMessage", "購票失敗：票務不足"  // 加入更具體的錯誤訊息
	        );
	    }
	  
		Optional<OrderDto> optionalOrder = orderRepositoryJdbc.findOrderDtoByRequestId(requestId);

			
		
		if (optionalOrder.isEmpty()) {
		    return Map.of("status", "輪尋中");
		}

		OrderDto order = optionalOrder.get();
        
        switch (order.getOrderStatus()) {
		            case "付款中":	return Map.of(
					                    "status", "付款中",
					                    "orderId", order.getOrderId());
		            
		            case "錯誤":		return Map.of(
						                "status", "錯誤",
						                "errorMessage", "訂單處理失敗");
		            
		            default:		return Map.of("status", "輪巡中");
        }
    }
	
	
	
	@Override
	public List<OrderDetailDto> getAllUserOrder(String userName) {

		Integer userId=userService.getUserId(userName);
		
		return orderRepositoryJdbc.findOrderDetail(userId);
		
	}

	//付款完更新訂單狀況
	@Override
	public void updateOrderStatus(Integer orderId) {
		
		orderRepositoryJdbc.updateOrderStatus(orderId);
		
	}

	//取消訂單
	@Override
	@Transactional
	public void cancelOrder(Integer orderId) {

		orderRepositoryJdbc.updateCancelOrder(orderId);
		
	}

	//訂單摘要
	@Override
	public OrderAstractDto getOrderAbstract(Integer orderId,String userName,String requestId) {

		validatePayTime(requestId);
		
	    OrderAstractDto dto = validateAndGetOrder(orderId);
		
		dto.setUserName(userName);	
	    log.info("獲取訂單摘要, 訂單ID: {}, 用戶名: {}, 請求ID: {}", orderId, userName);

		return dto;
	}


	@Override
	public OrderAstractDto getOrderAbstract2(Integer orderId, String userName) {
		
	    OrderAstractDto dto = validateAndGetOrder(orderId);

		dto.setUserName(userName);
		emailService.sendOrderEmail(dto, userName);
	    log.info("獲取訂單摘要, 訂單ID: {}, 用戶名: {}, 請求ID: {}", orderId, userName);

		return dto;
	}

	@Override
	public Integer createOrder(Integer userId, String section, Integer eventId, Integer quantity, String requestId) {

		return salesRepositoryJdbc.addTicketOrderWithSeat(userId, section, eventId, quantity, requestId,
				quantity);
	}

//=============================小組件	
	
	private OrderAstractDto validateAndGetOrder(Integer orderId) {
		
	    return Optional.ofNullable(
	    		orderRepositoryJdbc.findOrderAbstract(orderId))
	    		.orElseThrow(() -> 
	    				{            
	    					log.error("訂單不存在 orderId: {}", orderId);
	    				    return new RuntimeException("訂單找不到: " + orderId);});
	}

	private void validatePayTime(String requestId) {
		//我這寫在buyticket mq那裏		
		if(!redisService.exists(CacheKeys.Order.ORDER_PREFIX+requestId)) {
	        log.error("付款時間已過期 requestId: {}", requestId);
			throw new RuntimeException("付款時間結束");
		}
	}



	@Override
	public OrderDetailDto getOrderDetailByOrderId(Integer orderId) {
		
		
		return orderRepositoryJdbc.findOrderDetailByOrderId(orderId);
	}



	@Override
	public void refundTicketForm(RefundOrder dto) {

		refundService.handleRefund(dto);
		
	}
	
	
	
	
	
	
	
}
