package com.example.demo.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;
import com.example.demo.model.dto.orders.RefundOrder;
import com.example.demo.util.DatabaseUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OrderRepositoryJdbcImpl implements OrderRepositoryJdbc {

	
	private static final class SQL{
		static final String FIND_ORDER_DETAIL="""
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

				c.pic_event_ticket as picEventTicket,
				
			    h.host_name as hostName,

	    GROUP_CONCAT(p.pool_number) as poolNumbers

		FROM users u
		JOIN orders o ON o.user_id = u.user_id
		JOIN event e ON o.event_id = e.event_id
		JOIN host h ON e.host_id = h.host_id
		JOIN ticket t ON t.ticket_name = o.order_section AND t.event_id = o.event_id
		join pic c on c.pic_id=e.event_id
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
				    c.pic_event_ticket,
				    h.host_name
		ORDER BY o.order_datetime DESC
	
	""".trim();
		static final String FIND_ORDER_DETAIL_BY_ORDERID="""
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

				c.pic_event_ticket as picEventTicket,
				
			    h.host_name as hostName,

	    GROUP_CONCAT(p.pool_number) as poolNumbers

		FROM orders o
		JOIN users u ON o.user_id = u.user_id
		JOIN event e ON o.event_id = e.event_id
		JOIN host h ON e.host_id = h.host_id
		JOIN ticket t ON t.ticket_name = o.order_section AND t.event_id = o.event_id
		join pic c on c.pic_id=e.event_id
		LEFT JOIN pool p ON o.order_id = p.order_id
		WHERE o.order_id = ?
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
				    c.pic_event_ticket,
				    h.host_name
		ORDER BY o.order_datetime DESC
				""".trim();
		
		static final String FIND_ORDER_ABSTRACT="""
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
		static final String EXISTS_BY_REQUESTID="""
				SELECT COUNT(1) > 0
				FROM orders
				WHERE request_id = ?

				""".trim();
		static final String EXISTS_ORDERID_BY_REFUND="""
				select count(1)>0
				from refund 
				where order_id=?
				
				""".trim();
		
		
		static final String FIND_ORDERDTO_BY_REQUESTID= """
				SELECT order_id AS orderId,
			       order_status AS orderStatus
			FROM orders
			WHERE request_id = ?
			""".trim();
		static final String UPDATE_ORDER_STATUS="""
			    UPDATE orders
			    SET order_status = ?
			    WHERE order_id = ?
			""".trim();
		static final String UPDATE_CANCEL_ORDER_STATUS="""
				update 	pool
				set 	order_id =?,
				 		pool_status='未售出'
				where 	order_id=?
				""".trim();
	
		static final String ADD_REFUND_SUBMIT="""
				INSERT INTO refund(order_id, refund_title, refund_reason, refund_status)
				VALUES (?, ?, ?, '待處理')
	
				""".trim();
	
	
	
	
	
	}
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final RowMapper<OrderDetailDto> orderDetailMapper=new BeanPropertyRowMapper<>(OrderDetailDto.class);
	private static final RowMapper<OrderDto> orderMapper=new BeanPropertyRowMapper<>(OrderDto.class); 

	
	
	@Override
	public void addRefundSubmit(RefundOrder dto) {
		DatabaseUtils.executeUpdate(
				"addRefundSubmit",
					()->jdbcTemplate.update(SQL.ADD_REFUND_SUBMIT,dto.getOrderId(),dto.getRefundTitle(),dto.getRefundReason()),
					"新建退表table失敗");
		
		
	}
	
	
	@Override
	public OrderDetailDto findOrderDetailByOrderId(Integer orderId) {
	    return DatabaseUtils.executeQuery(
	        "findOrderDetailByOrderId",
	        () -> jdbcTemplate.queryForObject(
	            SQL.FIND_ORDER_DETAIL_BY_ORDERID,
	            (rs, rowNum) -> {
	                OrderDetailDto dto = orderDetailMapper.mapRow(rs, rowNum);
	                
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
	            orderId
	        ),
	        String.format("找不到訂單 ID:%d 的詳細資訊", orderId)
	    );
	}



	@Override
	public List<OrderDetailDto> findOrderDetail(Integer userId) {


		return DatabaseUtils.executeQuery(
				"findOrderDetail", 
	 			()->jdbcTemplate.query(
	 					SQL.FIND_ORDER_DETAIL,
			            (rs, rowNum) -> {
			                OrderDetailDto dto = orderDetailMapper.mapRow(rs, rowNum);
			                
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
			            },userId), 
	 			
	 			String.format("找不到用戶 ID:%d 的訂單詳細資訊", userId));

	}

	
	
	
//	訂單摘要
	@Override
	public OrderAstractDto findOrderAbstract(Integer orderId) {
		
		
		return DatabaseUtils.executeQuery(
				"findOrderAbstract", 
				
				()->jdbcTemplate.queryForObject(SQL.FIND_ORDER_ABSTRACT, (rs, rowNum) -> {
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
						return dto;}, 
						orderId), 
				
				String.format("找不到訂單ID:%d 的摘要", orderId));

	}	

	@Override
	public boolean existsByRequestId(String requestId) {

		return DatabaseUtils.executeQuery(
				"existsByRequestId", 
				()->jdbcTemplate.queryForObject(SQL.EXISTS_BY_REQUESTID, Boolean.class, requestId), 
				String.format("找不到RequestId:%s",requestId));
		
	}

	public Optional<OrderDto> findOrderDtoByRequestId(String requestId) {
			
			try {
		    	log.info("已搜尋到訂單"+jdbcTemplate.queryForObject(SQL.FIND_ORDERDTO_BY_REQUESTID, orderMapper, requestId));
		        return Optional.ofNullable(
		            jdbcTemplate.queryForObject(SQL.FIND_ORDERDTO_BY_REQUESTID, orderMapper, requestId)
		        );
		    } catch (EmptyResultDataAccessException e) {
		        log.debug("搜尋不到訂單...");
		        return Optional.empty();
		    } catch (Exception e) {
		        log.error("查詢訂單時出現未知錯誤，RequestID: {}, 錯誤: {}", requestId, e.getMessage(), e);
		        throw new RuntimeException("查詢訂單失敗", e);
		    }
		
		
		
	}

	public void updateOrderStatus(Integer orderId) {
		
		DatabaseUtils.executeUpdate(
					"updateOrderStatus", 
					()->jdbcTemplate.update(SQL.UPDATE_ORDER_STATUS, "訂單完成", orderId), 
					String.format("透過orderId:%d->更新失敗", orderId));

	}
	// 取消訂單
	@Override
	public void updateCancelOrder(Integer orderId) {
		
		
		DatabaseUtils.executeUpdate(
					"updateCancelOrder", 
					()->jdbcTemplate.update(SQL.UPDATE_ORDER_STATUS, "訂單取消", orderId), 
			        String.format("訂單 %d 取消失敗", orderId));
		DatabaseUtils.executeUpdate(
					"updateCancelOrder", 
					()->jdbcTemplate.update(SQL.UPDATE_CANCEL_ORDER_STATUS, null, orderId), 
					 String.format("訂單 %d 座位狀態更新失敗", orderId));
		
	}


	@Override
	public boolean existsOrderIdByRefund(Integer orderId) {

		return DatabaseUtils.executeQuery(
					"existsOrderIdByRefund", 
					()->jdbcTemplate.queryForObject(SQL.EXISTS_ORDERID_BY_REFUND, Boolean.class, orderId),
					"查詢是否有重複退票請求失敗"
					);
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
