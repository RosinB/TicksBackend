package com.example.demo.repository.order;

import java.util.List;
import java.util.Optional;

import com.example.demo.adminPanel.dto.orders.RefundSubmit;
import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.model.dto.orders.OrderDetailDto;
import com.example.demo.model.dto.orders.OrderDto;
import com.example.demo.model.dto.orders.RefundOrder;

public interface OrderRepositoryJdbc {
	
	
	OrderAstractDto	findOrderAbstract(Integer orderId);
	
	
	List<OrderDetailDto> findOrderDetail(Integer userId);

	OrderDetailDto findOrderDetailByOrderId(Integer orderId);
	
	
	boolean existsByRequestId(String requestId);
	
	Optional<OrderDto> findOrderDtoByRequestId(String requestId);
	
	void updateOrderStatus(Integer orderId) ;
	
	void updateCancelOrder(Integer orderId);
	
	void addRefundSubmit(RefundOrder dto);
	
	boolean existsOrderIdByRefund(Integer orderId);
	
	void updateRefundByOrderStatus(RefundOrder dto);
	
	
	
	
	
}
