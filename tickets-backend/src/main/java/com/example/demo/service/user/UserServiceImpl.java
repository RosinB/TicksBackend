package com.example.demo.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.util.Hash;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("userJDBC")
	UserRepositoryJdbc userRepositoryJdbc;
	
	
	@Autowired
	@Qualifier("userJPA")
	UserRepository userRepository;
	
	
	
	@Autowired
	UserMapper userMapper;
	
	public List<UserDto> getAllUser(){
		
		return userRepositoryJdbc .findAll().stream()
							  .map(userMapper::toDto)
							  .collect(Collectors.toList());
	
}

	@Override
	public void addUser(UserDto userDto) {

		User user=userMapper.toEnity(userDto);
		user.setSalt(Hash.getSalt());
		user.setUserPwdHash(Hash.getHash(userDto.getPassword(), user.getSalt()));
		int a=userRepositoryJdbc.addUser(user);
		System.out.println(a);	
	}

	@Override
	public Map<String, String> validateUserInput(UserDto userDto) {
		//用MAP去存重複資訊
		Map<String ,String > check=new HashMap<String, String>();
		
		if(!userRepository.existsByuserName(userDto.getUserName()))
		{	check.put("userName", "帳號重複");
		}
		
		
		return null;
	}
	
	
	
	
	
	
}
