package com.example.demo.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.annotation.Cacheable;
import com.example.demo.common.annotation.LoginCheck;
import com.example.demo.common.mapper.UserMapper;
import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;
import com.example.demo.model.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.service.common.EmailService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.CaptchaUtils;
import com.example.demo.util.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RedisService redisService;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final UserRepositoryJdbc userRepositoryJdbc;
	private final EmailService emailService;


	// 獲取所有用戶資訊
	@Cacheable(key = CacheKeys.User.ALL_USERS)
	@Override
	public List<UserDto> getAllUser() {

		return userRepositoryJdbc.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
	}

	// 根據用戶名查詢單一用戶資訊
	@Cacheable(key = "'"+CacheKeys.User.USERSDTO_PREFIX +"'+#userName")
	@Override
	public UserDto getUser(String userName) {

		return userRepository.findUserByUserName(userName).map(userMapper::toDto)
				.orElseThrow(() -> new RuntimeException("找不到UserName:" + userName));
	}

	// 驗證用戶登入 檢查用戶名存在性和密碼正確性
	@Override
	@LoginCheck
	public LoginResultDto checkUserLogin(LoginDto loginDto) {

		if (!userRepository.existsByUserName(loginDto.getUserName())) {
			return new LoginResultDto("帳號不存在");
		}

		String storeHashPassword = userRepository.findHashPasswordByUserName(loginDto.getUserName());
		if (!passwordEncoder.matches(loginDto.getPassword(), storeHashPassword)) {
			return new LoginResultDto("密碼錯誤");
		}

		loginDto.setUserId(userRepository.findIdByUserName(loginDto.getUserName()));
		return new LoginResultDto(loginDto);
	}

	// 更新用戶資料
	@Override
	@Transactional
	public void updateUser(UserUpdateDto userUpdateDto) {

		userRepository.updateUser(userUpdateDto.getUserPhone(), userUpdateDto.getUserEmail(),
				userUpdateDto.getUserBirthDate(), userUpdateDto.getUserName());

		clearUserCaches(userUpdateDto.getUserName());

	}

	// 新增用戶
	@Override
	public void addUser(UserDto userDto) {

		User user = userMapper.toEnity(userDto);
		user.setUserPwdHash(passwordEncoder.encode(userDto.getPassword()));
		userRepositoryJdbc.addUser(user);
		clearUserCaches(userDto.getUserName());

	}

	// 驗證用戶註冊資訊是否重複
	@Override
	public Map<String, String> validateUserInput(UserDto userDto) {
		// 用MAP去存重複資訊
		Map<String, String> errors = new HashMap<String, String>();

		List<User> conDuplicatesUsers = userRepository.findDuplicatesUsers(userDto.getUserName(),
				userDto.getUserPhone(), userDto.getUserEmail(), userDto.getUserIdCard());

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

//===============================驗證信箱相關=============================================
	// 查詢使用者信箱
	@Override
	@Cacheable(key = "'"+CacheKeys.User.USEREMAIL_PREFIX+"' + #userName")
	public String getEmail(String userName) {
		return userRepositoryJdbc.findUserEmailByUserName(userName);

	}

	// 生成並發送驗證碼
	@Override
	public void getCAPTCHA(String userName) {

		String code = CaptchaUtils.generateNumericCaptcha(6);
		String userEmail = getEmail(userName);
		System.out.println("getCaptcha"+userEmail);
		emailService.sendVerificationEmail(code, userName, userEmail);

	}

	// 驗證 Email 驗證碼
	@Override
	public String verificationEmail(String userName, String code) {

		return emailService.verificationEmail(code, userName);
	}

	// 忘記密碼 確認使用者和email的關係
	@Override
	public void checkUserAndEmail(String userName, String email) {

		String realEmail = getEmail(userName);

		if (!(email.equals(realEmail))) {
			log.info("email和userName不匹配");
			throw new RuntimeException("email和userName不匹配");
		}
		emailService.sendPasswordResetEmail(userName, realEmail);

	}

	// 驗證密碼重設 token 的有效性
	@Override
	public String checkToken(String token) {

		String userName = redisService.get(CacheKeys.User.USERTOKEN_PREFIX + token, String.class);
		if (userName == null) {
			log.info("token和帳號不匹配");
			throw new RuntimeException("token和帳號不匹配");
		}
		return userName;

	}

//===============================================尋找user相關的redis====================================
	@Override
	@Cacheable(key ="'"+ CacheKeys.User.USERID_PREFIX+"'+#userName" )
	public Integer getUserId(String userName) {
		 
	        return userRepository.findIdByUserName(userName);
	}
	

	@Override
	@Cacheable(key ="'"+  CacheKeys.User.USER_ISVERFIED_PREFIX+"'+#userName")
	public Boolean getUserIsVerified(String userName) {
	
		return userRepositoryJdbc.findUserIsVerifiedByUserName(userName);
	}

	private void clearUserCaches(String userName) {
		redisService.delete(CacheKeys.User.USERSDTO_PREFIX + userName);
		redisService.delete(CacheKeys.User.ALL_USERS);
	}

	
}
