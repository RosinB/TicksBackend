package com.example.demo.adminPanel.repository.host;

import com.example.demo.adminPanel.dto.event.EventDetailDto;

public interface AdminHostJDBC {

	
	int findHostIdByHostName(String hostName);
	
	void addPic(EventDetailDto dto , Integer eventId);
	
}
