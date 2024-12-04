package com.example.demo.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.annotation.CacheableUser;
import com.example.demo.common.annotation.GenerateCaptcha;
import com.example.demo.common.aspect.UserBusinessAspect;
import com.example.demo.common.mapper.UserMapper;
import com.example.demo.controller.UserController;
import com.example.demo.model.dto.login.LoginDto;
import com.example.demo.model.dto.login.LoginResultDto;
import com.example.demo.model.dto.user.UserDto;
import com.example.demo.model.dto.user.UserUpdateDto;
import com.example.demo.model.entity.user.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.GmailOAuthSender;
import com.example.demo.util.RedisService;
import com.google.api.services.gmail.Gmail;

@Transactional
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


	

	private  final UserRepository userRepository;
	private  final RedisService redisService;
	private  final PasswordEncoder passwordEncoder;
	private  final UserMapper userMapper;
	private  final UserRepositoryJdbc userRepositoryJdbc;

	public  UserServiceImpl(UserRepository userRepository,
							UserRepositoryJdbc userRepositoryJdbc,RedisService redisService,PasswordEncoder passwordEncoder,UserMapper userMapper) {
			this.userRepositoryJdbc=userRepositoryJdbc;
			this.redisService=redisService;
			this.passwordEncoder=passwordEncoder;
			this.userMapper=userMapper;
			this.userRepository=userRepository;
	}
	
	
	
	
	
	//查全部使用者
	@CacheableUser(key = CacheKeys.User.ALL_USERS , expireTime = 10 ,timeUnit = TimeUnit.MINUTES)
	@Override
	public List<UserDto> getAllUser() {
		
		return userRepositoryJdbc.findAll()
								 .stream()
								 .map(userMapper::toDto)
								 .collect(Collectors.toList());
	}

	//查單筆使用者
	@CacheableUser(key=CacheKeys.User.USERSDTO_PREFIX+"{0}",expireTime = 10 ,timeUnit = TimeUnit.MINUTES)
	@Override
	public UserDto getUser(String userName) {
	
		return userRepository.findUserByUserName(userName)
					  .map(userMapper::toDto)
					  .orElseThrow(()->  new RuntimeException("找不到UserName:"+userName)); 
		
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
	@Transactional  
	public void updateUser(UserUpdateDto userUpdateDto) {
		

		userRepository.updateUser(userUpdateDto.getUserPhone(), userUpdateDto.getUserEmail(), 
								  userUpdateDto.getUserBirthDate(), userUpdateDto.getUserName());
			
		 clearUserCaches(userUpdateDto.getUserName());
		
		
	}

	//新增使用者
	@Override
	public void addUser(UserDto userDto) {

		User user = userMapper.toEnity(userDto);		
		user.setUserPwdHash(passwordEncoder.encode(userDto.getPassword()));
		userRepository.save(user);
		clearUserCaches(userDto.getUserName());
	
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

	
	
//===============================驗證信箱相關=============================================
	//查詢使用者信箱
	@Override
	@CacheableUser(key = CacheKeys.User.USEREMAIL_PREFIX+"{0}" ,expireTime = 10,timeUnit = TimeUnit.MINUTES)
	public String getEmail(String userName) {

		return userRepositoryJdbc.findUserEmailByUserName(userName);
				
	}

	//獲取驗證碼
	@Override
    @GenerateCaptcha(length = 6)
	public void getCAPTCHA(String userName) {
		
        String code = UserBusinessAspect.getCaptcha();


        String cachekey=CacheKeys.User.USEREMAIL_PREFIX+ userName;
		String userEmail=redisService.get(cachekey, String.class);
		if(userEmail==null) {
			userEmail=userRepositoryJdbc.findUserEmailByUserName(userName);
			
			redisService.saveWithExpire(cachekey, userEmail, 10, TimeUnit.MINUTES);
		}
		
		
		String verificationKey=String.format(CacheKeys.User.VERIFICATION_CODE, userName,userEmail);
		
		
		redisService.saveWithExpire(verificationKey, code, 5, TimeUnit.MINUTES);
	
		
        try {
            Gmail service = GmailOAuthSender.getGmailService();         
        	//收件人者email "me"是關鍵字 不用改
        	GmailOAuthSender.sendMessage(service, "me", GmailOAuthSender.createEmail(userEmail, "信箱認證", 
        			"信箱驗證碼:"+code));
        
            System.out.println("郵件已成功寄出！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("郵件寄送失敗：" + e.getMessage());}
        
	}

	@Override
	public String verificationEmail(String userName, String code) {
		
		String userEmail=redisService.get(CacheKeys.User.USEREMAIL_PREFIX+userName, String.class);
		
		String verificationKey = String.format(CacheKeys.User.VERIFICATION_CODE, userName,userEmail);
		
		String verifCode=redisService.get(verificationKey, String.class);
		
		if(code.trim().equals(verifCode.trim())) 
		{	
			userRepositoryJdbc.updateUserIsVerified(userName);		
			clearUserCaches(userName);
			return "驗證成功";		
		}
		
		return "驗證失敗";
	}

	
	
	//忘記密碼 確認使用者和email的關係
	@Override
	public String checkUserAndEmail(String userName, String email) {

		
		String realEmail=userRepositoryJdbc.findUserEmailByUserName(userName);
		
		if( !(email.equals(realEmail) ) ) { 
			logger.info("email和userName不匹配");
			throw new RuntimeException("email和userName不匹配");
		}
		
		
		try {
            Gmail service = GmailOAuthSender.getGmailService();         
            
            String token=UUID.randomUUID().toString();
            
            //把userName和token綁在redis
            String savaUserNameForToken="userName:"+token;
            //把token和userNmae綁在redis
            String saveTokenForUserName="token:"+userName;
            
            redisService.saveWithExpire(savaUserNameForToken, userName, 10, TimeUnit.MINUTES);
            redisService.saveWithExpire(saveTokenForUserName, token, 10, TimeUnit.MINUTES);
            
            
            GmailOAuthSender.sendMessage(service, "me", GmailOAuthSender.createEmail(email, "重設密碼", 
        			"""
        			重設密碼:
        			請去以下網址重設:
        				http://localhost:3000/forgetpassword/reset/%s
    
                    這是您的重設密碼url，有效期限10分鐘。
        		    """.formatted(token))
        			
        			);
        
            System.out.println("重設密碼郵件已成功寄出！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("郵件寄送失敗：" + e.getMessage());}
		
		
		return null;
	}

	
	//確認url的token關係
	@Override
	public String checkToken(String token) {
		
        String userName=redisService.get(CacheKeys.User.USERTOKEN_PREFIX+token, String.class);
        if(userName==null) {
        	logger.info("token和帳號不匹配");
        	throw new RuntimeException("token和帳號不匹配");
        }
        
        return userName;
	
		
	}
		
	
	
	
	
	
	private void clearUserCaches(String userName) {
	    redisService.delete(CacheKeys.User.USERSDTO_PREFIX + userName);
	    redisService.delete(CacheKeys.User.ALL_USERS);
	}
	
	
	
	
}
	
	

	
	
	
	

	
	

