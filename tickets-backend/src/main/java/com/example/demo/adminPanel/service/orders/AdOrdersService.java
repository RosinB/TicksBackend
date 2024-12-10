package com.example.demo.adminPanel.service.orders;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.dto.orders.RefundSubmit;

public interface AdOrdersService {

	
		List<AdOrdersDto> getAllOrdersByEventId(Integer eventId);
		
		List<RefundSubmit> getAllRefund();
		
		void rejectRefund(Integer refundId);
		
		void successRefund(Integer refundId);
		
}
