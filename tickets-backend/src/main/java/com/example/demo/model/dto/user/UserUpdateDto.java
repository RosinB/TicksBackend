package com.example.demo.model.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserUpdateDto {
	
	@Size(min = 4, max = 10, message = "名字長度必須在 4 到 10 個字符之間")
	private String userName;
	
	@Email(message = "郵箱格式不正確")

	private String userEmail;
	
	@Pattern(regexp = "^[+]?\\d{1,15}$", message = "電話號碼格式無效")
	private String userPhone;
	private LocalDate userBirthDate;
		
	
}
