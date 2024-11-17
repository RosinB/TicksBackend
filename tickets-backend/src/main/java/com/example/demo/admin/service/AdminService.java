package com.example.demo.admin.service;

import java.util.List;

import com.example.demo.admin.dto.HostDto;

public interface AdminService {

	
	List<HostDto> getAllHost();
	
	void addHost(HostDto data);
}
