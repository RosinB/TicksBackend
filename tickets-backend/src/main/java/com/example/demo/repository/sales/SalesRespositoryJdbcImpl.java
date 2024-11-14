package com.example.demo.repository.sales;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.dto.ticket.TicketDto;
import com.example.demo.model.dto.ticket.TicketSectionDto;

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

		
		List<TicketDto> ticketDto= jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TicketDto.class), eventId);

		if(ticketDto.isEmpty()) {
				logger.info(" findPriceAndStatusByEventId的回傳為空");
				return null;
		}

		return ticketDto;



	}

}
