package com.example.demo.adminPanel.service.traffic;

import java.util.List;
import java.util.Set;


public interface TrafficService {

	void blockUserName(String userName);
	void unblockUserName(String userName);
	Set<String> getBlockedUserNames();
	void blockIpAddress(String ipAddress);

}
