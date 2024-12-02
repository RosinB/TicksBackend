package com.example.demo.repository.order;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;

public interface OrderRepositoryJdbc {
	
	
	OrderAstractDto	findOrderAbstract(Integer orderId);
	
	
	List<OrderDetailDto> findOrderDetail(Integer orderId);

	boolean existsByRequestId(String requestId);
	
	Optional<OrderDto> findOrderDtoByRequestId(String requestId);
	
	void updateOrderStatus(Integer orderId) ;
	
	void updateCancelOrder(Integer orderId);
	
	
}
