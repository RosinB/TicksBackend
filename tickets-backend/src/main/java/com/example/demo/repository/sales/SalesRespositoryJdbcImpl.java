package com.example.demo.repository.sales;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
						t.ticket_isAvailable as ticketIsAvailable

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

	@Override
	public void checkTicketAndUpdate(String section, Integer eventId, Integer quantity) {
		String sql = """
				UPDATE 	ticket
				SET 	ticket_remaining = ticket_remaining - ?,
				    	ticket_isAvailable = CASE
				                            	WHEN ticket_remaining - ? = 0
				                            	THEN false
				                            	ELSE true
				                         	 END
				WHERE 	ticket_name = ?
				  AND 	event_id = ?
				  AND	ticket_remaining >= ?
				  AND 	ticket_isAvailable = true;

								""".trim();

		try {
			int result = jdbcTemplate.update(sql, quantity, quantity, section, eventId, quantity);

			if (result < 1) {
				logger.warn("票務更新失敗，eventId: {}, section: {}, quantity: {}", eventId, section, quantity);
				throw new RuntimeException("票務不足或不可用");
			}
		} catch (DataAccessException e) {
			logger.error("SQL 執行出現異常，section: {}, eventId: {}, quantity: {}, 錯誤信息: {}", section, eventId, quantity,
					e.getMessage(), e);
			throw new RuntimeException("數據庫操作失敗", e);
		}
	}

	// 檢查看看票務的狀況
	@Override
	public CheckSectionStatusDto checkSectionStatus(String section, Integer eventId) {
		String sql = """
					select
							ticket_name 		as section,
							ticket_remaining 	as ticketRemaining,
							ticket_isAvailable 	as ticketIsAvailable
					from 	ticket
					where 	ticket_name =? and event_id=?
				""".trim();

		try {

			CheckSectionStatusDto dto = jdbcTemplate.queryForObject(sql,
					new BeanPropertyRowMapper<>(CheckSectionStatusDto.class), section, eventId);

			
			return dto;

		} catch (EmptyResultDataAccessException e) {
			System.out.println("查詢結果為空，section: " + section + ", eventId: " + eventId);
			return null;
			
		} catch (Exception e) {
			System.out.println("其他異常: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

}
