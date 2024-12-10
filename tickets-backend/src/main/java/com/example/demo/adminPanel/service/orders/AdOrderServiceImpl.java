package com.example.demo.adminPanel.service.orders;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.dto.orders.RefundSubmit;
import com.example.demo.adminPanel.repository.orders.AdOrdersJDBC;
import com.example.demo.adminPanel.service.common.AdRefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdOrderServiceImpl implements AdOrdersService{

	
	
	private final AdOrdersJDBC adOrdersJDBC;
	private final AdRefundService refundService;
	@Override
	public List<AdOrdersDto> getAllOrdersByEventId(Integer eventId) {
		
		return adOrdersJDBC.findAllOrdersByEventId(eventId);
		
	}

	@Override
	public List<RefundSubmit> getAllRefund() {

		return adOrdersJDBC.findRefundByPending();
		
		
	}

	@Override
	public void rejectRefund(Integer refundId) {
		refundService.RejectRefundOrder(refundId);
		
	}

	@Override
	public void successRefund(Integer refundId) {
		refundService.successRefundOrder(refundId);
		
	}
	
	
	
	
	
	
}
