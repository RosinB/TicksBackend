package com.example.demo.service.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;

public interface UserService {
	//查全部使用者
	List<UserDto> getAllUser();
	
	//添加單筆使用者
	void addUser(UserDto userDto);
	
	//使用者登入檢查
	LoginResultDto checkUserLogin(LoginDto loginDto);
	
	//透過userName 獲得userDto資訊
	UserDto getUser(String userName);
	
	//更新使用者資料
	void  updateUser(UserUpdateDto userUpdateDto);
	
	//註冊帳號檢查帳號是否重複
	Map<String,String> validateUserInput(UserDto userDto);
	
	//===============================驗證信箱相關=============================================

	String getEmail(String userName);
	
	void getCAPTCHA(String userName);
	
//==================	驗證信箱
	String verificationEmail(String userName,String code);
	
	
//=================忘記密碼 -驗證信箱============================

	String checkUserAndEmail(String userName,String email);
	
	String checkToken(String token);
	

}
