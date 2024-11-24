package com.example.demo.service.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.order.OrderRepositoryJdbc;
import com.example.demo.util.RedisService;

@Service
public class OrderServiceImpl implements OrderService{
	private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	OrderRepositoryJdbc orderRepositoryJdbc;
	@Autowired
    private RedisService redisService;
	
	
	public Map<String, Object> getTicketStatus(String requestId) {
        // 檢查是否存在對應的記錄
		String redisStatus = redisService.get("order:" + requestId,String.class);
		
	    if ("FAILED".equals(redisStatus)) {
	        return Map.of(
	            "status", "FAILED",
	            "errorMessage", "購票失敗"
	        );
	    }
		Optional<OrderDto> optionalOrder = orderRepositoryJdbc.findOrderDtoByRequestId(requestId);
		
		if (optionalOrder.isEmpty()) {
		    return Map.of("status", "PENDING");
		}

		OrderDto order = optionalOrder.get();
        
        switch (order.getOrderStatus()) {
		            case "COMPLETED":
		                return Map.of(
		                    "status", "COMPLETED",
		                    "orderId", order.getOrderId()
		                );
		            case "FAILED":
		                return Map.of(
		                    "status", "FAILED",
		                    "errorMessage", "訂單處理失敗"
		                );
		            default:
		                return Map.of("status", "PENDING");
        }
    }
	
	
	
	@Override
	public List<OrderDetailDto> getAllUserOrder(String userName) {

		Integer userId=userRepository.findIdByUserName(userName);
		
		System.out.println("UserID是"+userId);
		List<OrderDetailDto> dto=orderRepositoryJdbc.findOrderDetail(userId);
		
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("找不到詳細資訊"));
		
		
		
		return dto;
	}




	//訂單摘要
	@Override
	public OrderAstractDto getOrderAbstract(Integer orderId,String userName) {

		
		OrderAstractDto dto= orderRepositoryJdbc.findOrderAbstract(orderId);
		
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("訂單找不到"));
		
		dto.setUserName(userName);
		
		
		return dto;
	}

	
	
	
	
}
