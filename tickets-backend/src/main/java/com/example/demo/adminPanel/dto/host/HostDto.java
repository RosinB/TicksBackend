package com.example.demo.adminPanel.dto.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HostDto {
	private Integer hostId;

	private String hostName;

	private String hostContact;

	private String hostDescription;
}
