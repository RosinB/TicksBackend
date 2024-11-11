package com.example.demo.model.entity.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	@Id
	@Column(name = "event_id")
	private Integer eventId;
	
	@Column(name = "event_performer")
	private String eventPerformer;
	
	@Column(name = "event_name")
	private String eventName;

	@Column(name = "event_description")
	private String eventDescription;

	@Column(name = "event_date")
	private LocalDateTime eventDate;

	@Column(name = "event_price")
	private BigDecimal eventPrice;

	@Column(name = "event_location")
	private String eventLocation;

	@Column(name = "event_type")
	private String eventType;

	@Column(name = "event_total_tickets")
	private Integer eventTotalTickets;

	@Column(name = "event_status")
	private String eventStatus;

	@Column(name = "host_id")
	private Integer hostId;

}
