package com.example.demo.repository.order;

import com.example.demo.model.dto.orders.OrderAstractDto;

public interface OrderRepositoryJdbc {
	
	
	OrderAstractDto	findOrderAbstract(Integer eventId,Integer userId);

}
