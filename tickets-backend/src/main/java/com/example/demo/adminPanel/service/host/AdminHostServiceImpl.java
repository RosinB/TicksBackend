package com.example.demo.adminPanel.service.host;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.host.HostDto;
import com.example.demo.adminPanel.repository.HostRepo;
import com.example.demo.model.entity.host.Host;
import com.example.demo.util.RedisService;

@Service
public class AdminHostServiceImpl implements AdminHostService{

	private final static Logger log= LoggerFactory.getLogger(AdminHostServiceImpl.class);
	
	@Autowired
	HostRepo hostRepository;
	//查詢所有主辦
	
	@Autowired
	private RedisService redisService;
	
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
	
	public void updateHost(HostDto data) {
		
		String cacheKey="Host:detail:"+data.getHostId();
		
		Host host =redisService.get(cacheKey, Host.class);
		
		if(host==null) {
			
			host =hostRepository.findById(data.getHostId()).orElseThrow(()-> new RuntimeException("host資料庫抓不到"));
		}
		
		host.setHostName(data.getHostName());
		host.setHostDescription(data.getHostDescription());
		host.setHostContact(data.getHostContact());
		
		try {
			hostRepository.save(host);
			System.out.println("host更新成功");

		} catch (Exception e) {
			throw new RuntimeException("更新失敗");

		}
		
		redisService.save(cacheKey, host);
		
		
				
		
	}

	
	
	
	
}
