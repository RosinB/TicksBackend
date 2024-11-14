package com.example.demo.repository.sales;


import java.util.List;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;

public interface SalesRepositoryJdbc {

	SalesDto findSalesDetailByEventId(Integer eventId);
	
	List<TicketDto> findPriceAndStatusByEventId(Integer eventId);
}
