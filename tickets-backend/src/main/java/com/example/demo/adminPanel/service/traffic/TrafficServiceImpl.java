package com.example.demo.adminPanel.service.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.util.CacheKeys;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficServiceImpl implements TrafficService {

	private final RedisService redisService;

	@Override
	public void blockUserName(String userName) {

		redisService.sadd(CacheKeys.util.BLOCK, userName);
		log.info("使用者{}已經被封鎖", userName);

	}

	@Override
	public void unblockUserName(String userName) {
	
		redisService.srem(CacheKeys.util.BLOCK, userName);
		log.info("使用者已經解封{}", userName);

	}

	@Override
	public Set<String> getBlockedUserNames() {
        return redisService.sMembers(CacheKeys.util.BLOCK);

	}

	@Override
	public void blockIpAddress(String ipAddress) {
		redisService.sadd(CacheKeys.util.BLOCK_IPS, ipAddress);
		log.info("使用者{}已經被封鎖", ipAddress);

		
	}
	
	
	
	

}
