package com.example.demo.model.entity.sales;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="sales")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Sales {
	
	@Id
	@Column(name="sales_id")
	private Integer salesId;
	
	@Column(name="sales_remaining")
	private Integer salesRemaining;

	@Column(name ="sales_status")
	private String salesStatus;
	
	@Column(name ="event_id")
	private Integer eventId;
	
}
