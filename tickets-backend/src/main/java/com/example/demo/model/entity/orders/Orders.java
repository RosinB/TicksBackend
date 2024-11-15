package com.example.demo.model.entity.orders;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="orders")
@Data
public class Orders {

		@Id
		@Column(name="order_id")
//		訂單id
		private Integer orderId;
			
		@Column(name="event_id")
//		活動id
		private Integer eventId;	
		
		@Column(name="user_id")
//		使用者id
		private Integer userId;
		
		@Column(name = "order_quantity")
//		票卷數量
		private Integer orderQuantity;
		

			
		@Column(name = "order_section")
//		票卷區域
		private String orderSection;
		
		@Column(name ="order_status")
//		票卷狀態
		private String orderStatus;
		
		@Column(name="order_datetime")
//		訂單創立時間
		private LocalDateTime orderDateTime;
	
	
}
