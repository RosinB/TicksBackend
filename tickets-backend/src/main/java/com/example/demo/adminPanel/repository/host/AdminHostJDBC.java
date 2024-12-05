package com.example.demo.adminPanel.repository.host;

import java.util.List;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.host.HostDto;

public interface AdminHostJDBC {

	
	int findHostIdByHostName(String hostName);
	
	void addPic(EventDetailDto dto , Integer eventId);
	
	void updatePic(EventDetailDto dto , Integer eventId);

	void addHost(HostDto data);
	
	void updateHost(HostDto data);
	
	List<HostDto> findAllHost();
}
