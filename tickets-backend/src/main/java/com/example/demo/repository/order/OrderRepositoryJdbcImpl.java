package com.example.demo.repository.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;

@Repository
public class OrderRepositoryJdbcImpl implements OrderRepositoryJdbc {

	private final static Logger logger = LoggerFactory.getLogger(OrderRepositoryJdbcImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<OrderDetailDto> findOrderDetail(Integer userId) {

		String sql = """
					SELECT
						    o.order_id as orderId,
						    o.order_quantity as orderQuantity,
						    o.order_section as orderSection,
						    o.order_datetime as orderDateTime,
						    o.order_status as orderStatus,
							o.request_id as requestId,
						    e.event_name as eventName,
						    e.event_performer as eventPerformer,
						    e.event_date as eventDate,
						    e.event_time as eventTime,
						    e.event_location as eventLocation,
		
						    t.ticket_price as ticketPrice,
		
						    h.host_name as hostName,

				    GROUP_CONCAT(p.pool_number) as poolNumbers

					FROM users u
					JOIN orders o ON o.user_id = u.user_id
					JOIN event e ON o.event_id = e.event_id
					JOIN host h ON e.host_id = h.host_id
					JOIN ticket t ON t.ticket_name = o.order_section AND t.event_id = o.event_id
					LEFT JOIN pool p ON o.order_id = p.order_id
					WHERE u.user_id = ?
					GROUP BY 
							    o.order_id,
							    o.order_quantity,
							    o.order_section,
							    o.order_datetime,
							    o.order_status,
							    o.request_id ,
							    e.event_name,
							    e.event_performer,
							    e.event_date,
							    e.event_time,
							    e.event_location,
							    t.ticket_price,
							    h.host_name
					ORDER BY o.order_datetime DESC
				
				""".trim();

		 try {
		        List<OrderDetailDto> dtos = jdbcTemplate.query(sql,
		            (rs, rowNum) -> {
		                OrderDetailDto dto = new BeanPropertyRowMapper<>(OrderDetailDto.class).mapRow(rs, rowNum);
		                
		                // 處理多個 poolNumbers
		                String poolNumbers = rs.getString("poolNumbers");
		                if (poolNumbers != null && !poolNumbers.isEmpty()) {
		                    // 將逗號分隔的字串轉換成個別的 pool number
		                    String[] numbers = poolNumbers.split(",");
		                    for (String number : numbers) {
		                        dto.addPoolNumber(Integer.parseInt(number.trim()));
		                    }
		                }
		                
		                return dto;
		            },
		            userId);
		            
		        return dtos;
		        
		    } catch (BadSqlGrammarException e) {
		        logger.error("SQL 語法錯誤: {}", e.getMessage());
		        return null;
		    } catch (DataAccessException e) {
		        logger.error("數據訪問錯誤: {}", e.getMessage());
		        return null;
		    } catch (Exception e) {
		        logger.error("未知錯誤: {}", e.getMessage());
		        return null;
		    }

	}

//	訂單摘要

	@Override
	public OrderAstractDto findOrderAbstract(Integer orderId) {
		String sql = """
				      SELECT
					    o.order_id as orderId,
					    o.order_quantity as orderQuantity,
					    o.order_section as orderSection,
					    o.order_status as orderStatus,
					    o.order_datetime as orderDateTime,
					    e.event_name as eventName,
					    t.ticket_price as orderPrice,
					    GROUP_CONCAT(p.pool_number) as poolNumbers
				FROM orders o
				JOIN event e ON o.event_id = e.event_id
				JOIN ticket t ON t.event_id = o.event_id AND t.ticket_name = o.order_section
				JOIN pool p ON o.order_id = p.order_id
				WHERE o.order_id = ?
				GROUP BY
							    o.order_id,
							    o.order_quantity,
							    o.order_section,
							    o.order_status,
							    o.order_datetime,
							    e.event_name,
							    t.ticket_price
				  """.trim();

		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
				OrderAstractDto dto = new OrderAstractDto();
				dto.setOrderId(rs.getInt("orderId"));
				dto.setEventName(rs.getString("eventName"));
				dto.setOrderSection(rs.getString("orderSection"));
				dto.setOrderStatus(rs.getString("orderStatus"));
				dto.setOrderDateTime(rs.getTimestamp("orderDateTime").toLocalDateTime());
				dto.setOrderPrice(rs.getInt("orderPrice"));

				// 處理多個座位號碼
				String poolNumbersStr = rs.getString("poolNumbers");
				if (poolNumbersStr != null) {
					for (String poolNumber : poolNumbersStr.split(",")) {
						dto.addPoolNumber(Integer.parseInt(poolNumber.trim()));
					}
				}

				return dto;
			}, orderId);
		} catch (Exception e) {
			logger.info("訂單摘要查詢失敗");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean existsByRequestId(String requestId) {
		String sql = """
				SELECT COUNT(1) > 0
				FROM orders
				WHERE request_id = ?

				""";

		try {
			return jdbcTemplate.queryForObject(sql, Boolean.class, requestId);
		} catch (Exception e) {
			// 捕獲可能的異常
			logger.error("查詢是否存在 requestId 時出現錯誤: {}", e.getMessage(), e);
			throw new RuntimeException("查詢失敗", e);
		}
	}

	public Optional<OrderDto> findOrderDtoByRequestId(String requestId) {
		String sql = """
				SELECT order_id AS orderId,
				       order_status AS orderStatus
				FROM orders
				WHERE request_id = ?
				""";

		try {
			logger.info("已搜尋到訂單"
					+ jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(OrderDto.class), requestId));
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(OrderDto.class), requestId));
		} catch (EmptyResultDataAccessException e) {
			logger.info("搜尋不到訂單...");
			return Optional.empty();
		} catch (Exception e) {
			logger.error("查詢訂單時出現未知錯誤，RequestID: {}, 錯誤: {}", requestId, e.getMessage(), e);
			throw new RuntimeException("查詢訂單失敗", e);
		}
	}

	public void updateOrderStatus(Integer orderId) {
		String sql = """
				    UPDATE orders
				    SET order_status = ?
				    WHERE order_id = ?
				""";

		try {
			jdbcTemplate.update(sql, "訂單完成", orderId);
		} catch (Exception e) {
			logger.error("updateOrderStatus時出現錯誤: ", e.getMessage());
			throw new RuntimeException("updateOrderStatus出現錯誤 " + e.getMessage());
		}
	}

	// 取消訂單
	@Override
	public void updateCancelOrder(Integer orderId) {
		String sql = """
				    UPDATE orders
				    SET order_status = ?
				    WHERE order_id = ?
				""";
		String cancel = """
				update 	pool
				set 	order_id =?,
				 		pool_status='未售出'
				where 	order_id=?
				""".trim();

		try {
			jdbcTemplate.update(sql, "訂單取消", orderId);
			jdbcTemplate.update(cancel, null, orderId);
		} catch (Exception e) {
			logger.error("updateCancelOrder時出現錯誤: ", e.getMessage());
			throw new RuntimeException("updateCancelOrder出現錯誤 " + e.getMessage());
		}
	}

	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
}
