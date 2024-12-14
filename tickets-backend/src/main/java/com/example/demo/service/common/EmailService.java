package com.example.demo.service.common;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.orders.OrderAstractDto;
import com.example.demo.repository.user.UserRepositoryJdbc;
import com.example.demo.service.user.UserService;
import com.example.demo.util.CacheKeys;
import com.example.demo.util.CaptchaUtils;
import com.example.demo.util.GmailOAuthSender;
import com.example.demo.util.RedisService;
import com.google.api.services.gmail.Gmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final RedisService redisService;
	private final UserRepositoryJdbc userRepositoryJdbc;
	@Lazy
	@Autowired
	private UserService userService;

	
	public void sendOrderEmail(OrderAstractDto dto,String userName) {
		String userEmail = userService.getEmail(userName);

		try {
			String emailContent = """
					親愛的 %s 您好,

					感謝您購買音樂會門票！以下是您的訂單明細：

					訂單資訊
					------------------
					訂單編號：#%d
					活動名稱：%s
					訂單時間：%s
					訂單狀態：%s

					座位資訊
					------------------
					區域：%s
					座位：%s
					總金額：NT$ %d

					重要提醒
					------------------
					1. 請在演出當天提前到場
					2. 入場時請出示有效證件
					3. 如有任何問題，請聯繫客服

					祝您觀賞愉快！

					此為系統自動發送郵件，請勿直接回覆。
					""".formatted(
					    dto.getUserName(),
					    dto.getOrderId(),
					    dto.getEventName(),
					    dto.getOrderDateTime().toString(),
					    dto.getOrderStatus(),
					    dto.getOrderSection(),
					    dto.getSeatsDisplay(),
					    dto.getOrderPrice()
					);
			
			
			Gmail service = GmailOAuthSender.getGmailService();
			GmailOAuthSender.sendMessage(service, "me", GmailOAuthSender.createEmail(
					userEmail, 
					String.format("訂單確認 - %s (#%d)", dto.getEventName(), dto.getOrderId()), 
					emailContent)

			);
			log.info("重設密碼信已成功寄出 {}", userEmail);
		} catch (Exception e) {
			log.warn("郵件發送失敗", e);
			throw new RuntimeException("郵件發送失敗", e);
		}
		
	}
	
	
	
	public void sendVerificationEmail(String userName) {
		String code = CaptchaUtils.generateNumericCaptcha(6);
		String userEmail = userService.getEmail(userName);

		String verificationKey = String.format(CacheKeys.User.VERIFICATION_CODE, userName, userEmail);

		redisService.saveWithExpire(verificationKey, code, 5, TimeUnit.MINUTES);

		try {
			Gmail service = GmailOAuthSender.getGmailService();
			// 收件人者email "me"是關鍵字 不用改
			GmailOAuthSender.sendMessage(service, "me",
					GmailOAuthSender.createEmail(userEmail, "信箱認證", "信箱驗證碼:" + code));

			log.info("認證信已成功寄出{}", userEmail);
		} catch (Exception e) {
			log.warn("郵件發送失敗", e);
			throw new RuntimeException("郵件發送失敗", e);
		}

	}

	public String verificationEmail(String code, String userName) {
		String userEmail = redisService.get(CacheKeys.User.USEREMAIL_PREFIX + userName, String.class);

		String verificationKey = String.format(CacheKeys.User.VERIFICATION_CODE, userName, userEmail);

		String verifCode = redisService.get(verificationKey, String.class);

		if (code.trim().equals(verifCode.trim())) {
			userRepositoryJdbc.updateUserIsVerified(userName);
			clearUserCaches(userName);
			return "驗證成功";
		}

		return "驗證失敗";

	}

	public void sendPasswordResetEmail(String userName, String email) {
		String token = UUID.randomUUID().toString();
		saveTokenToRedis(userName, token);

		try {

			Gmail service = GmailOAuthSender.getGmailService();
			GmailOAuthSender.sendMessage(service, "me", GmailOAuthSender.createEmail(email, "重設密碼", """
					重設密碼:
					請去以下網址重設:
						http://localhost:3000/forgetpassword/reset/%s

					         這是您的重設密碼url，有效期限10分鐘。
					   """.formatted(token))

			);
			log.info("重設密碼信已成功寄出 {}", email);
		} catch (Exception e) {
			log.warn("郵件發送失敗", e);
			throw new RuntimeException("郵件發送失敗", e);
		}

	}

	private void clearUserCaches(String userName) {
		redisService.delete(CacheKeys.User.USERSDTO_PREFIX + userName);
		redisService.delete(CacheKeys.User.ALL_USERS);
	}

	private void saveTokenToRedis(String userName, String token) {

		// 把userName和token綁在redis
		String savaUserNameForToken = CacheKeys.User.USERNAME_PREFIX + token;
		// 把token和userNmae綁在redis
		String saveTokenForUserName = CacheKeys.User.USERTOKEN_PREFIX + userName;

		redisService.saveWithExpire(savaUserNameForToken, userName, 10, TimeUnit.MINUTES);
		redisService.saveWithExpire(saveTokenForUserName, token, 10, TimeUnit.MINUTES);

	}
}
