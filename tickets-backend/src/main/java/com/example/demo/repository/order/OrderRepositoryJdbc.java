package com.example.demo.repository.order;

import java.util.List;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;

public interface OrderRepositoryJdbc {
	
	
	OrderAstractDto	findOrderAbstract(Integer orderId);
	
	
	List<OrderDetailDto> findOrderDetail(Integer orderId);

}
