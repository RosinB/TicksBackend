package com.example.demo.service.order;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
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
