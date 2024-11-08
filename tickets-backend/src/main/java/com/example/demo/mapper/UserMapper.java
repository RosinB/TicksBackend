package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.entity.user.User;

@Component
public class UserMapper {

	
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	public UserDto toDto(User user) {
//		Enity轉Dto
		return modelMapper.map(user,UserDto.class);
	}
	public User toEnity(UserDto userdto) {
//		Dto轉Enity
		return modelMapper.map(userdto,User.class);
	}
	
	
	
	
	
}
