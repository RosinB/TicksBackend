package com.example.demo.service.user;

import java.util.List;

import com.example.demo.model.dto.user.UserDto;

public interface UserService {

	List<UserDto> getAllUser();
	
	void addUser(UserDto userDto);
	
}
