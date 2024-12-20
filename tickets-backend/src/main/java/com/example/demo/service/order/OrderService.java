package com.example.demo.service.order;

import java.util.List;
import java.util.Map;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.RefundOrder;

public interface OrderService {

	
	OrderAstractDto getOrderAbstract(Integer orderId,String userName,String requestId);
	
	OrderAstractDto getOrderAbstract2(Integer orderId,String userName);

	
	List<OrderDetailDto> getAllUserOrder(String userName);
	
	Map<String, Object> getTicketStatus(String requestId);
	
	void updateOrderStatus(Integer orderId);
	
	void cancelOrder(Integer orderId);
	
	Integer createOrder(Integer userId, String section, Integer eventId,Integer quantity, String requestId);
	
	OrderDetailDto getOrderDetailByOrderId(Integer orderId);
	
	void refundTicketForm(RefundOrder dto);
	
	
}