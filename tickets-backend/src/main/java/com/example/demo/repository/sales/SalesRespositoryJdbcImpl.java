package com.example.demo.repository.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.example.demo.model.dto.sales.CheckSectionStatusDto;
import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;

@Repository
@Qualifier("SalesJDBC")
public class SalesRespositoryJdbcImpl implements SalesRepositoryJdbc {

	private final static Logger logger = LoggerFactory.getLogger(SalesRespositoryJdbcImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	// 透過活動ID獲得售票狀態
	@Override
	public SalesDto findSalesDetailByEventId(Integer eventId) {
		String sql = """
					select 	s.sales_id as salesId,
							s.sales_status as salesStatus,
							e.event_id as eventId

					from 	sales s
					join 	event e
					on  	s.event_id=e.event_id
					where 	e.event_Id=?

				""".trim();

		SalesDto salesDto = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SalesDto.class), eventId);

		if (Optional.of(salesDto).isEmpty()) {
			logger.info("演唱會Id匹配不到銷售id");

			return null;
		}

		return salesDto;
	}

	// 透過活動ID抓到販賣價錢
	@Override
	public List<TicketDto> findPriceAndStatusByEventId(Integer eventId) {
		String sql = """
				select
						t.ticket_price as ticketPrice,
						t.ticket_name as ticketName,
						t.ticket_isAvailable as ticketIsAvailable,
						t.ticket_remaining as ticketRemaining

				from 	ticket t
				where 	t.event_id = ?
				""".trim();

		List<TicketDto> ticketDto = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TicketDto.class), eventId);

		if (ticketDto.isEmpty()) {
			logger.info(" findPriceAndStatusByEventId的回傳為空");
			return null;
		}

		return ticketDto;

	}

	// ==========================添加order========================================
	@Override
	public void addTicketOrder(Integer userId, String section, Integer eventId, Integer quantity, String requestId) {
		String sql = """
				insert into orders(event_id,user_id,order_quantity,order_section,order_status,request_id)
							values(?,?,?,?,?,?)
				""".trim();

		String updatePoolSql = """
				    UPDATE pool
				    SET order_id = ?, pool_status = '已售出'
				    WHERE event_id = ? AND event_section = ? AND pool_status = '未售出'
				    LIMIT ?
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, eventId);
				ps.setInt(2, userId);
				ps.setInt(3, quantity);
				ps.setString(4, section);
				ps.setString(5, "付款中");
				ps.setString(6, requestId);
				return ps;
			}, keyHolder);

		} catch (Exception e) {
			throw new RuntimeException("獲取訂單key失敗");
		}
		Integer orderId = keyHolder.getKey().intValue();

		try {
			logger.info("參數: orderId={}, eventId={}, section={}, quantity={}", orderId, eventId, section, quantity);

			int rowsUpdated = jdbcTemplate.update(updatePoolSql, orderId, eventId, section, quantity);

			if (rowsUpdated < 1) {
				logger.warn("座位更新失敗，更新行數: {}", rowsUpdated);
				throw new RuntimeException("座位更新失敗，沒有更新任何行");
			}

			logger.info("座位更新成功，更新行數: {}", rowsUpdated);

		} catch (DataAccessException e) {
			logger.error("數據庫操作失敗，請檢查 SQL 和參數。", e);
			throw new RuntimeException("座位更新失敗，數據庫操作出錯", e);

		} catch (Exception e) {
			logger.error("未知錯誤，座位更新失敗", e);
			throw new RuntimeException("座位更新失敗，系統錯誤", e);
		}

	}

	@Override
	public Integer addTicketOrderWithSeat(Integer userId, String section, Integer eventId, Integer quantity,
			String requestId, Integer seat) {
		String sql = """
				insert into orders(event_id,user_id,order_quantity,order_section,order_status,request_id)
							values(?,?,?,?,?,?)
				""".trim();

		

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, eventId);
				ps.setInt(2, userId);
				ps.setInt(3, quantity);
				ps.setString(4, section);
				ps.setString(5, "付款中");
				ps.setString(6, requestId);
				return ps;
			}, keyHolder);

		} catch (Exception e) {
			throw new RuntimeException("獲取訂單key失敗");
		}
	//==========================================================================================================	
		Integer orderId = keyHolder.getKey().intValue();

		return orderId;
		
	}

	
	
	
	@Override
	public void updateTicketOrderSeat(Integer userId, String section, Integer eventId, Integer seat,Integer orderId) {
		String updatePoolSql = """
			    UPDATE pool
			    SET order_id = ?, pool_status = '已售出'
			    WHERE event_id = ? AND event_section = ? AND pool_status = '未售出' and pool_number=?
			   
			""";
		try {
			logger.info("參數: orderId={}, eventId={}, section={}, poolNumber={}", orderId, eventId, section, seat);
			
			int rowsUpdated = jdbcTemplate.update(updatePoolSql, orderId, eventId, section,seat);
			logger.info("訂單新增成功:參數: orderId={}, eventId={}, section={}, poolNumber={}", orderId, eventId, section, seat);

			if (rowsUpdated != 1) {
	            throw new RuntimeException("座位更新失敗，可能座位已被他人購買");
	        }


		} catch (Exception e) {
			logger.error("未知錯誤，座位更新失敗", e);
			throw new RuntimeException("座位更新失敗，系統錯誤", e);
		}		
	}

	// =======================================================================================
	// ============================處理訂票狀況======================================
	@Override
	public void checkTicketAndUpdate(String section, Integer eventId, Integer quantity) {
		String sql = """
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

		try {
			int result = jdbcTemplate.update(sql, quantity, section, eventId, quantity);
			if (result < 1) {
				logger.warn("票務更新失敗，eventId: {}, section: {}, quantity: {}", eventId, section, quantity);
				throw new RuntimeException("票務不足或不可用");
			}
		} catch (DataAccessException e) {
			logger.error("SQL 執行出現異常，section: {}, eventId: {}, quantity: {}, 錯誤信息: {}", section, eventId, quantity,
					e.getMessage(), e);
			throw new RuntimeException("數據庫操作失敗" + e.getMessage());
		}
	}
	// ============================處理訂票狀況======================================

	// 檢查看看票務的狀況
	@Override
	public Boolean checkSectionStatus(String section, Integer eventId) {
		String sql = """
					select
							ticket_isAvailable 	as ticketIsAvailable
					from 	ticket
					where 	ticket_name =? and event_id=?
				""".trim();

		try {

			Boolean hasTickets = jdbcTemplate.queryForObject(sql, boolean.class, section, eventId);

			return hasTickets;

		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("票卷查詢狀態結果為空，section: " + section + ", eventId: " + eventId);

		} catch (Exception e) {
			throw new RuntimeException("其他異常");
		}

	}

	@Override
	public Integer findRemaingByEventIdAndSection(Integer eventId, String section) {
		String sql = """
					select ticket_remaining
					from ticket
					where event_id =?
					  and ticket_name=?
				""";
		try {
			return jdbcTemplate.queryForObject(sql, Integer.class, eventId, section);
		} catch (Exception e) {
			logger.info("findRemaingByEventIdAndSection搜尋有錯", e.getMessage());
			throw new RuntimeException("findRemaingByEventIdAndSection搜尋有錯" + e.getMessage());
		}

	}

	// 看位置有沒有人坐
	@Override
	public boolean existsSeatsByPoolNumber(Integer seat, String section, Integer eventId) {
		String sql = """
				SELECT COUNT(1) > 0
				from pool

				where pool_number=?
				and   event_section=?
				and   event_id=?
				and   pool_status='未售出'
				""".trim();

		try {
			return jdbcTemplate.queryForObject(sql, Boolean.class, seat, section, eventId);

		} catch (Exception e) {
			logger.info("existsSeatsByPoolNumber有錯誤" + e.getMessage());
			throw new RuntimeException("existsSeatsByPoolNumber有錯誤" + e.getMessage());
		}

	}

}
