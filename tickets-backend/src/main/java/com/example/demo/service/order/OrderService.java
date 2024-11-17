package com.example.demo.service.order;

import java.util.List;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;

public interface OrderService {

	
	OrderAstractDto getOrderAbstract(Integer orderId,String userName);
	
	
	List<OrderDetailDto> getAllUserOrder(String userName);
	
	
}
