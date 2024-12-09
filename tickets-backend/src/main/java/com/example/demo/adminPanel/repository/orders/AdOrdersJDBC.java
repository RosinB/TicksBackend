package com.example.demo.adminPanel.repository.orders;

import java.util.List;

import com.example.demo.adminPanel.dto.orders.AdOrdersDto;
import com.example.demo.adminPanel.dto.orders.RefundSubmit;

public interface AdOrdersJDBC {

	List<AdOrdersDto> findAllOrdersByEventId(Integer eventId);
	
	Integer updateOrderByUpdateTime(Integer eventId,String section);

	List<RefundSubmit> findRefundByPending();
	
}
