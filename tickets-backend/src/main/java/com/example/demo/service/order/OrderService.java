package com.example.demo.service.order;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;

public interface OrderService {

	
	OrderAstractDto getOrderAbstract(PostTicketSalesDto dto);
	
	
}
