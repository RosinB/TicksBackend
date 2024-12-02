package com.example.demo.service.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
	            "status", "錯誤",
	            "errorMessage", "購票失敗"
	        );
	    }
	    
		Optional<OrderDto> optionalOrder = orderRepositoryJdbc.findOrderDtoByRequestId(requestId);

		if (optionalOrder.isEmpty()) {
		    return Map.of("status", "輪尋中");
		}

		OrderDto order = optionalOrder.get();
        
        switch (order.getOrderStatus()) {
		            case "付款中":
		                return Map.of(
		                    "status", "付款中",
		                    "orderId", order.getOrderId()
		                );
		            case "錯誤":
		                return Map.of(
		                    "status", "錯誤",
		                    "errorMessage", "訂單處理失敗"
		                );
		            default:
		                return Map.of("status", "輪巡中");
        }
    }
	
	
	
	@Override
	public List<OrderDetailDto> getAllUserOrder(String userName) {

		String cacheKey="userId:"+userName;
		Integer userId=redisService.get(cacheKey, Integer.class);
		if(userId==null) {
			 userId=userRepository.findIdByUserName(userName);
			 redisService.saveWithExpire(cacheKey, userId, 10, TimeUnit.MINUTES);
	
		}
		

		List<OrderDetailDto> dto=orderRepositoryJdbc.findOrderDetail(userId);
		
		System.out.println(dto);
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("找不到詳細資訊"));
		
		
		
		return dto;
	}

	//付款完更新訂單狀況
	@Override
	public void updateOrderStatus(Integer orderId) {
		
		orderRepositoryJdbc.updateOrderStatus(orderId);
		
	}

	//取消訂單
	@Override
	public void cancelOrder(Integer orderId) {

		orderRepositoryJdbc.updateCancelOrder(orderId);
		
	}




	//訂單摘要
	@Override
	public OrderAstractDto getOrderAbstract(Integer orderId,String userName,String requestId) {

		//我這寫在buyticket那裏		
		if(!redisService.exists("order:"+requestId)) {
			logger.info("付款時間結束");
			throw new RuntimeException("付款時間結束");
		}
		
		OrderAstractDto dto= orderRepositoryJdbc.findOrderAbstract(orderId);
		
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("訂單找不到"));
		
		dto.setUserName(userName);
		
		
		return dto;
	}



	@Override
	public OrderAstractDto getOrderAbstract2(Integer orderId, String userName) {
		OrderAstractDto dto= orderRepositoryJdbc.findOrderAbstract(orderId);
		
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("訂單找不到"));
		
		dto.setUserName(userName);
		
		
		return dto;
	}



	


	
	
	
	
	
}
