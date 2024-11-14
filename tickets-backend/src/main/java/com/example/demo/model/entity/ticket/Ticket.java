package com.example.demo.model.entity.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

	@Id
	@Column(name="ticket_id")
	 private Integer ticketId;
	
	@Column(name ="event_id")
	private Integer eventId;
	
	@Column(name ="ticket_name")
	private String ticketName;
	
	@Column(name="ticket_price")
	private Integer ticketPrice;
	
	@Column(name="ticket_quantity")
	private Integer ticketQuantity;
	
	@Column(name="ticket_remaining")
	private Integer ticketRemaining;
	
	@Column(name = "ticket_isAvailable")
	private Boolean ticketIsAvailable =true;
	
	
	
}
