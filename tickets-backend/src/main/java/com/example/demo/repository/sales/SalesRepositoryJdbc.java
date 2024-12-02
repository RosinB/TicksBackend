package com.example.demo.repository.sales;


import java.util.List;

import com.example.demo.model.dto.sales.CheckSectionStatusDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;

public interface SalesRepositoryJdbc {

	SalesDto findSalesDetailByEventId(Integer eventId);
	
	List<TicketDto> findPriceAndStatusByEventId(Integer eventId);
	
	Boolean checkSectionStatus(String section,Integer eventId);
	
	void checkTicketAndUpdate(String section,Integer eventId, Integer quantity);

	void addTicketOrder(Integer userId,String section ,Integer eventId ,Integer quantity,String requestId);
	
	Integer findRemaingByEventIdAndSection(Integer eventId,String section);	
	
	boolean existsSeatsByPoolNumber(Integer seat ,String section,Integer eventId);
	
	Integer addTicketOrderWithSeat(Integer userId,String section ,Integer eventId ,Integer quantity,String requestId,Integer poolNumber);

	void updateTicketOrderSeat(Integer userId, String section, Integer eventId, Integer seat,Integer orderId);
	
	
	


}
