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
import com.example.demo.model.dto.login.LoginDto;
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
	
//查全部使用者
	public List<UserDto> getAllUser() {

		return userRepositoryJdbc.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());

	}


	@Override
	public LoginDto checkUserLogin(LoginDto loginDto) {
		
		if(userRepository.existsByUserName(loginDto.getUserName())) {
			
			String salt=userRepository.findSaltByUserName(loginDto.getUserName());
			String HashPassword=Hash.getHash(loginDto.getPassword(), salt);		
			if(userRepository.findHashPasswordByUserName(loginDto.getUserName()).equals(HashPassword))
			{		
				return loginDto;
			}	
		}
		
		return null;
	}





	//新增使用者
	@Override
	public void addUser(UserDto userDto) {

		User user = userMapper.toEnity(userDto);
		user.setSalt(Hash.getSalt());
		user.setUserPwdHash(Hash.getHash(userDto.getPassword(), user.getSalt()));
		int a = userRepositoryJdbc.addUser(user);
		System.out.println(a);
	}
//檢查註冊使用者資料是否重複
	@Override
	public Map<String, String> validateUserInput(UserDto userDto) {
		// 用MAP去存重複資訊
		Map<String, String> errors = new HashMap<String, String>();

		
		
		List<User> conDuplicatesUsers = userRepository.findDuplicatesUsers(userDto.getUserName(), userDto.getUserPhone(),
				userDto.getUserEmail(), userDto.getUserIdCard());

		for (User user : conDuplicatesUsers) {
			if (user.getUserName().equals(userDto.getUserName())) {
				errors.put("userName", "帳號重複");
			}
			if (user.getUserPhone().equals(userDto.getUserPhone())) {
				errors.put("userPhone", "手機重複");
			}
			if (user.getUserEmail().equals(userDto.getUserEmail())) {
				errors.put("userEmail", "電子信箱重複");
			}
			if (user.getUserIdCard().equals(userDto.getUserIdCard())) {
				errors.put("userIdCard", "身分證重複");
			}
		}

		return errors;

	}

}
