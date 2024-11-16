package com.example.demo.service.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.sales.PostTicketSalesDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.order.OrderRepositoryJdbc;

@Service
public class OrderServiceImpl implements OrderService{
	private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	OrderRepositoryJdbc orderRepositoryJdbc;
	
	@Override
	public OrderAstractDto getOrderAbstract(PostTicketSalesDto order) {
		Integer eventId = order.getEventId();
		String userName = order.getUserName();
		
		Integer userId =userRepository.findIdByUserName(userName);
		
		
		
		
		return orderRepositoryJdbc.findOrderAbstract(eventId, userId);
	}

	
	
	
	
}
