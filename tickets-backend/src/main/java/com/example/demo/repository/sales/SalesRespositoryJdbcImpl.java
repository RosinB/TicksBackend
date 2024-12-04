package com.example.demo.repository.sales;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.util.DatabaseUtils;

@Repository
@Qualifier("SalesJDBC")
public class SalesRespositoryJdbcImpl implements SalesRepositoryJdbc {

	private final static Logger logger = LoggerFactory.getLogger(SalesRespositoryJdbcImpl.class);
	

	private static final class SQL{
		 static final String FIND_SALES_DETAIL_BY_EVENT_ID="""
				select 	s.sales_id as salesId,
				s.sales_status as salesStatus,
				e.event_id as eventId

		from 	sales s
		join 	event e
		on  	s.event_id=e.event_id
		where 	e.event_Id=?

	""".trim();
		 static final String FIND_PRICE_AND_STATUS_BY_EVENT_ID="""
				select
							t.ticket_price as ticketPrice,
							t.ticket_name as ticketName,
							t.ticket_isAvailable as ticketIsAvailable,
							t.ticket_remaining as ticketRemaining

					from 	ticket t
					where 	t.event_id = ?
					""".trim();
		 static final String ADD_TICKET_ORDER="""
				insert into orders(event_id,user_id,order_quantity,order_section,order_status,request_id)
				values(?,?,?,?,?,?)
	""".trim();
		 static final String UPDATE_ORDER_POOL="""
			    UPDATE pool
			    SET order_id = ?, pool_status = '已售出'
			    WHERE event_id = ? AND event_section = ? AND pool_status = '未售出'
			    LIMIT ?
			""".trim();
		 static final String ADD_TICKET_ORDER_WITH_SEAT="""
					insert into orders(event_id,user_id,order_quantity,order_section,order_status,request_id)
					values(?,?,?,?,?,?)
		""".trim();
		 static final String UPDATE_TICKET_ORDER_SEAT="""
				    UPDATE pool
				    SET order_id = ?, pool_status = '已售出'
				    WHERE event_id = ? AND event_section = ? AND pool_status = '未售出' and pool_number=?
				   
				""";
		 static final String UPDATE_TICKET_AND_CHECK="""
					UPDATE 	ticket
					SET 	ticket_remaining = ticket_remaining - ?,
					    	ticket_isAvailable = CASE
					                            	WHEN ticket_remaining <= 0
					                            	THEN false
					                            	ELSE true
					                         	 END
					WHERE 	ticket_name = ?
					  AND 	event_id = ?
					  AND	ticket_remaining >= ?
					  AND 	ticket_isAvailable = true;

									""".trim();
		 static final String CHECK_SECTION_AND_STATUS="""
					select
					ticket_isAvailable 	as ticketIsAvailable
			from 	ticket
			where 	ticket_name =? and event_id=?
		""".trim();
		 static final String FIND_REMAINING_BY_EVENT_ID_AND_SECTION= """
					select ticket_remaining
					from ticket
					where event_id =?
					  and ticket_name=?
				""".trim();
		 static final String EXISTS_SEATS_BY_POOL_NUMBER="""
					SELECT COUNT(1) > 0
					from pool

					where pool_number=?
					and   event_section=?
					and   event_id=?
					and   pool_status='未售出'
					""".trim();
	}

	private static final RowMapper<SalesDto> salesMapper=new BeanPropertyRowMapper<>(SalesDto.class);
	private static final RowMapper<TicketDto> ticketMapper=new BeanPropertyRowMapper<>(TicketDto.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	// 透過活動ID獲得售票狀態
	@Override
	public SalesDto findSalesDetailByEventId(Integer eventId) {
		
		return DatabaseUtils.executeQuery(
					"findSalesDetailByEventId", 
					()->jdbcTemplate.queryForObject(SQL.FIND_SALES_DETAIL_BY_EVENT_ID, salesMapper, eventId), 
					"演唱會Id匹配不到");

	}

	// 透過活動ID抓到販賣價錢
	@Override
	public List<TicketDto> findPriceAndStatusByEventId(Integer eventId) {
	
		return DatabaseUtils.executeQuery(
					"findPriceAndStatusByEventId", 
					()->jdbcTemplate.query(SQL.FIND_PRICE_AND_STATUS_BY_EVENT_ID, ticketMapper, eventId), 
					"票價和狀態找不到");

		
	}

	// ==========================添加order========================================
	@Override
	public void addTicketOrder(Integer userId, String section, Integer eventId, Integer quantity, String requestId) {
	
		KeyHolder keyHolder = new GeneratedKeyHolder();

		DatabaseUtils.executeQuery(
				"addTicketOrder", 
				()->jdbcTemplate.update(connection -> {
						PreparedStatement ps = connection.prepareStatement(SQL.ADD_TICKET_ORDER, Statement.RETURN_GENERATED_KEYS);
						ps.setInt(1, eventId);
						ps.setInt(2, userId);
						ps.setInt(3, quantity);
						ps.setString(4, section);
						ps.setString(5, "付款中");
						ps.setString(6, requestId);
						return ps;
					}, keyHolder), 	
				"獲取訂單key失敗");
		
		
		Integer orderId = keyHolder.getKey().intValue();
		logger.info("參數: orderId={}, eventId={}, section={}, quantity={}", orderId, eventId, section, quantity);

		DatabaseUtils.executeUpdate(
				"addTicketOrder", 
				()->jdbcTemplate.update(SQL.UPDATE_ORDER_POOL, orderId, eventId, section, quantity), 
				"座位更新失敗");
		
		
		

	}

	@Override
	public Integer addTicketOrderWithSeat(Integer userId, String section, Integer eventId, Integer quantity,
			String requestId, Integer seat) {
		

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DatabaseUtils.executeQuery(
				"addTicketOrderWithSeat", 
				()->jdbcTemplate.update(connection -> {
						PreparedStatement ps = connection.prepareStatement(SQL.ADD_TICKET_ORDER_WITH_SEAT, Statement.RETURN_GENERATED_KEYS);
						ps.setInt(1, eventId);
						ps.setInt(2, userId);
						ps.setInt(3, quantity);
						ps.setString(4, section);
						ps.setString(5, "付款中");
						ps.setString(6, requestId);
						return ps;
					}, keyHolder), 
				"獲取訂單key失敗");
		
		Integer orderId = keyHolder.getKey().intValue();

		return orderId;
		
	}

	
	@Override
	public void updateTicketOrderSeat(Integer userId, String section, Integer eventId, Integer seat,Integer orderId) {
	
		
		DatabaseUtils.executeUpdate(
						"updateTicketOrderSeat", 
						()->jdbcTemplate.update(SQL.UPDATE_TICKET_ORDER_SEAT, orderId, eventId, section,seat), 
						"座位更新失敗，系統錯誤");
		
		logger.info("訂單新增成功:參數: orderId={}, eventId={}, section={}, poolNumber={}", orderId, eventId, section, seat);
		
		
			
	}

	
	// ============================處理訂票狀況======================================
	@Override
	public void checkTicketAndUpdate(String section, Integer eventId, Integer quantity) {

		DatabaseUtils.executeUpdate(
						"checkTicketAndUpdate", 
						()->jdbcTemplate.update(SQL.UPDATE_TICKET_AND_CHECK, quantity, section, eventId, quantity), 
						"數據庫操作失敗");
			
	}
	// ============================處理訂票狀況======================================

	// 檢查看看票務的狀況
	@Override
	public Boolean checkSectionStatus(String section, Integer eventId) {
		
		return DatabaseUtils.executeQuery(
					"checkSectionStatus", 
					()->jdbcTemplate.queryForObject(SQL.CHECK_SECTION_AND_STATUS, boolean.class, section, eventId), 
					"票卷查詢狀態結果為空，section: " + section + ", eventId: " + eventId);
	}

	@Override
	public Integer findRemaingByEventIdAndSection(Integer eventId, String section) {

		return DatabaseUtils.executeQuery(
					"findRemaingByEventIdAndSection", 
					()->jdbcTemplate.queryForObject(SQL.FIND_REMAINING_BY_EVENT_ID_AND_SECTION, Integer.class, eventId, section), 
					"查詢剩票失敗");

	}

	// 看位置有沒有人坐
	@Override
	public boolean existsSeatsByPoolNumber(Integer seat, String section, Integer eventId) {
	
		return DatabaseUtils.executeQuery(
					"existsSeatsByPoolNumber", 
					()->jdbcTemplate.queryForObject(SQL.EXISTS_SEATS_BY_POOL_NUMBER, Boolean.class, seat, section, eventId), 
					"查詢位置失敗");

	}

}
