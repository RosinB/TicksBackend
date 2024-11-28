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
import com.example.demo.util.GmailOAuthSender;
import com.example.demo.util.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.services.gmail.Gmail;

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

	
	
//===============================驗證信箱相關=============================================
	//查詢使用者信箱
	@Override
	public String getEmail(String userName) {

		String cachekey="userEmail:"+ userName;
		String userEmail=redisService.get(cachekey, String.class);
		
		if(userEmail==null) {
			userEmail=userRepositoryJdbc.findUserEmailByUserName(userName);
			
			redisService.saveWithExpire(cachekey, userEmail, 10, TimeUnit.MINUTES);
		}
		
		return userEmail;
				
				
	}

	//獲取驗證碼
	@Override
	public void getCAPTCHA(String userName) {
		Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 生成 0-9 的隨機數
        }
        
        String cachekey="userEmail:"+ userName;
		String userEmail=redisService.get(cachekey, String.class);
		if(userEmail==null) {
			userEmail=userRepositoryJdbc.findUserEmailByUserName(userName);
			
			redisService.saveWithExpire(cachekey, userEmail, 10, TimeUnit.MINUTES);
		}
		
		
		String cachekey2="userName:"+userName+"userEmail"+userEmail+"code:";

		redisService.saveWithExpire(cachekey2, code, 5, TimeUnit.MINUTES);
	
		
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
		
		String userEmail=redisService.get("userEmail:"+userName, String.class);
		
		String cachekey="userName:"+userName+"userEmail"+userEmail+"code:";

		
		String verifCode=redisService.get(cachekey, String.class);
		
		if(code.trim().equals(verifCode.trim())) 
		{	
			userRepositoryJdbc.updateUserIsVerified(userName);		
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
		
        String savaUserNameForToken="userName:"+token;
        String userName=redisService.get(savaUserNameForToken, String.class);
        if(userName==null) {
        	logger.info("token和帳號不匹配");
        	throw new RuntimeException("token和帳號不匹配");
        }
        
        return userName;
	
		
	}
		
	
	
	
	
	
}
	
	

	
	
	
	

	
	

