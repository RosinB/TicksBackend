package com.example.demo.model.entity.host;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "host")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Host {
	@Id
	@Column(name = "host_id")
	private Integer hostId;

	@Column(name = "host_name")
	private String hostName;

	@Column(name = "host_contact")
	private String hostContact;

	@Column(name = "host_description")
	private String hostDescription;

}
