package com.example.demo.service.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.user.UserRepositoryJdbc;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepositoryJdbc userRepositoryJdbc;
	@Autowired
	UserMapper userMapper;
	
	public List<UserDto> getAllUser(){
		
		return userRepositoryJdbc .findAll().stream()
							  .map(userMapper::toDto)
							  .collect(Collectors.toList());
	
}}
