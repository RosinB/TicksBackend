package com.example.demo.adminPanel.repository.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.ticket.TicketDtos;
import com.example.demo.util.DatabaseUtils;

@Repository

public class AdTicketJDBCImpl implements AdTicketJDBC{
	
	private static final class SQL{
		static final String ADD_TICKETDTOS_BY_EVENTID="""
				insert into ticket(event_id,		ticket_name,	ticket_price,
				   ticket_quantity,	ticket_remaining)
values(?,?,?,?,?)

""".trim();
		static final String UPDATE_TICKETDTOS_BY_EVENTID=	"""
				update 	ticket
				set	   	ticket_price 	=?,
					   	ticket_quantity =?
				where 	event_id 	=? and ticket_name =?		
			""".trim();
		static final String UPDATE_REMAINING="""
				update ticket 
				set	   ticket_remaining=ticket_remaining + ?
				where  event_id=?
				and    ticket_name=?
			""".trim();
	}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void addTicketDtosByEventId(Integer eventId,TicketDtos ticketDtos) {
		
			DatabaseUtils.executeUpdate(
						"addTicketDtosByEventId", 
						()->jdbcTemplate.update(SQL.ADD_TICKETDTOS_BY_EVENTID,
												eventId,ticketDtos.getTicketSection(),ticketDtos.getTicketPrice(),
												ticketDtos.getTicketQuantity(),ticketDtos.getTicketQuantity()), 
						String.format("新增TicketDto錯誤，eventId:%d",eventId ));
				
	}

	
	
	@Override
	public void updateTicketDtosByEventId(Integer eventId, TicketDtos ticketDtos) {
		
			DatabaseUtils.executeUpdate(
						"updateTicketDtosByEventId",
						()->jdbcTemplate.update(SQL.UPDATE_TICKETDTOS_BY_EVENTID,
												ticketDtos.getTicketPrice(),ticketDtos.getTicketQuantity(),
												eventId,ticketDtos.getTicketSection()),
						String.format("更新TicketDto有問題，eventId:%d", eventId));
			
	}
	
	@Override
	public void updateRemaining(Integer eventId, Integer tickets,String section) {
		
		
			DatabaseUtils.executeUpdate("updateRemaining",
						()-> jdbcTemplate.update(SQL.UPDATE_REMAINING,tickets,eventId,section), 
						String.format("更新票務失敗，eventId=%d", eventId));
	}

	
	
}
