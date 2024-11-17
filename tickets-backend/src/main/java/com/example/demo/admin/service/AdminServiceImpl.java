package com.example.demo.admin.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.admin.dto.HostDto;
import com.example.demo.admin.repository.jpa.HostRepository;
import com.example.demo.model.entity.host.Host;

@Service
public class AdminServiceImpl implements AdminService{

	private final static Logger log= LoggerFactory.getLogger(AdminServiceImpl.class);
	
	@Autowired
	HostRepository hostRepository;
	//查詢所有主辦
	@Override
	public List<HostDto> getAllHost() {

		List<HostDto> dto =hostRepository.findAllHosts();
		Optional.ofNullable(dto).orElseThrow(()->new RuntimeException("找不到host"));
		

		return dto;
	}
	@Override
	public void addHost(HostDto data) {
		
		Host host=new Host();
		host.setHostName(data.getHostName());
		host.setHostContact(data.getHostContact());
		host.setHostDescription(data.getHostDescription());
		try {
			hostRepository.save(host);

		} catch (Exception e) {
			throw new RuntimeException("資料庫新增失敗");
		}
		
	}

	
	
	
	
}
