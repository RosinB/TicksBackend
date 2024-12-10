package com.example.demo.adminPanel.service.common;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdRefundService {
	
	private final AdOrdersJDBC adOrdersJDBC;
	
	@Transactional
	public void RejectRefundOrder(Integer refundId) {
		
		adOrdersJDBC.updateRefundByReject(refundId);
		log.info("(駁回)退票訂單更新成功:{}",refundId);
		
		
	}
	@Transactional
	public void  successRefundOrder(Integer refundId) {
		adOrdersJDBC.updateRefundBySuccess(refundId);
		log.info("(通過)退票訂單更新成功:{}",refundId);
		
	}
	
	
	
}
