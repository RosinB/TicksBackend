package com.example.demo.adminPanel.service.host;

import java.util.List;

import com.example.demo.adminPanel.dto.host.HostDto;


public interface AdminHostService {

	
	List<HostDto> getAllHost();
	
	void addHost(HostDto data);
	void updateHost(HostDto data);
}
