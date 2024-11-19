package com.example.demo.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.UserController;
import com.example.demo.exception.User.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;
import com.example.demo.model.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;

@Transactional
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


	@Autowired
	@Qualifier("userJDBC")
	UserRepositoryJdbc userRepositoryJdbc;

	@Autowired
	@Qualifier("userJPA")
	UserRepository userRepository;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	UserMapper userMapper;
	
	
	//查全部使用者
	public List<UserDto> getAllUser() {
//		String  cacheKey = "AllUser";
//		 UserDto=redisService.get(cacheKey, new TypeReference<List<UserDto>>(){});
//		if(UserDto!=null) return UserDto;
		

		try {
			List<UserDto>	UserDto=userRepositoryJdbc.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
//			redisService.save(cacheKey, UserDto);
			return UserDto;

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		
	}

	//查單筆使用者
	@Override
	public UserDto getUser(String userName) {
		String cacheKey="userDto:"+userName;
			
		UserDto cacheUserDto=redisService.get(cacheKey,UserDto.class);
		redisService.delete(cacheKey);
		if(cacheUserDto!= null) return cacheUserDto;
	
		UserDto userDto=userRepository.findUserByUserName(userName)
	 			  					  .map(userMapper::toDto)
	 			  					  .orElseThrow(()->  new RuntimeException("找不到UserName:"+userName)); 
		redisService.saveWithExpire(cacheKey, userDto ,1,TimeUnit.HOURS);
		
		return userDto;
		

	 
	}


	//登入驗證 沒優化========================================
	@Override
	public  LoginResultDto checkUserLogin(LoginDto loginDto) {
	
		if(!userRepository.existsByUserName(loginDto.getUserName())) {
			//找到salt
			return new LoginResultDto(false,"帳號不存在",null);	
		}
		
		
		String storeHashPassword=userRepository.findHashPasswordByUserName(loginDto.getUserName());
		
		
		if(!passwordEncoder.matches(loginDto.getPassword(), storeHashPassword))
		{		
			return new LoginResultDto(false,"密碼錯誤",null);
		}
		
		loginDto.setUserId(userRepository.findIdByUserName(loginDto.getUserName()));
		
		
		
		return new LoginResultDto(true,"登入正確",loginDto);
	}
	// 沒優化========================================



	
	

	//更新使用者資料 電話 email 生日
	@Override
	public String updateUser(UserUpdateDto userUpdateDto) {
		

		int updateRow=userRepository.updateUser(userUpdateDto.getUserPhone(), userUpdateDto.getUserEmail(), 
												userUpdateDto.getUserBirthDate(), userUpdateDto.getUserName());
		
		logger.info(userUpdateDto.getUserName()+" 更新資料筆數 "+updateRow+"筆");
		
		
		if(updateRow==0) {return "更新使用者失敗";}
		 redisService.delete("userDto:" + userUpdateDto.getUserName());
	     redisService.delete("AllUser");
		
		
		return "更新成功";
	}

	//新增使用者
	@Override
	public void addUser(UserDto userDto) {

		User user = userMapper.toEnity(userDto);		

		user.setUserPwdHash(passwordEncoder.encode(userDto.getPassword()));

		try {
			userRepository.save(user);

		} catch (Exception e) {
			throw new RuntimeException("使用者新增出現問題");
		}
	
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
