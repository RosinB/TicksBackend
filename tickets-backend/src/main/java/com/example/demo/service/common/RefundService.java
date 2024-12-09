package com.example.demo.service.common;

import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.RefundOrder;
import com.example.demo.repository.order.OrderRepositoryJdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {
	private final OrderRepositoryJdbc orderRepositoryJdbc;
	
	public void handleRefund(RefundOrder dto) {
		
		if(!orderRepositoryJdbc.existsOrderIdByRefund(dto.getOrderId())) {
			
			log.info("重複退票請求:{}",dto.getOrderId());
			throw new RuntimeException("重複退票請求");
		}
		
		orderRepositoryJdbc.addRefundSubmit(dto);
		
		
		
	}
	
}
