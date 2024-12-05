package com.example.demo.adminPanel.service.host;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.host.HostDto;
import com.example.demo.adminPanel.repository.host.AdminHostJDBC;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminHostServiceImpl implements AdminHostService{

	
	private final AdminHostJDBC adminHostJDBC;
	
	@Override
	public List<HostDto> getAllHost() {

		return adminHostJDBC.findAllHost();
	}
	
	
	
	@Override
	public void addHost(HostDto data) {
		
		adminHostJDBC.addHost(data);
	}
	
	
	public void updateHost(HostDto data) {
		
		adminHostJDBC.updateHost(data);
				
		
	}

	
	
	
	
}
