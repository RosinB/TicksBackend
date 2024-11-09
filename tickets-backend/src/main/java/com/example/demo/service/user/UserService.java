package com.example.demo.service.user;

import java.util.List;
import java.util.Map;

import com.example.demo.model.dto.user.UserDto;

public interface UserService {

	List<UserDto> getAllUser();
	
	void addUser(UserDto userDto);
	
	Map<String,String> validateUserInput(UserDto userDto);
	
}
