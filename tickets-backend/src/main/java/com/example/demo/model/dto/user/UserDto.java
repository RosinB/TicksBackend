package com.example.demo.model.dto.user;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
	
	private Integer userId;

	@Size(min = 4, max = 10, message = "名字長度必須在 4 到 10 個字符之間")
	private String userName;

	private String password;
	
	@Pattern(regexp = "^[+]?\\d{10}$", message = "電話號碼必須是 10 位數字")
	private String userPhone;
   
	@Email(message = "郵箱格式不正確")
	private String userEmail;

	private LocalDate userBirthDate;
	
    @Pattern(regexp = "^[A-Z][0-9]{9}$", message = "身份證格式不正確")
	private String userIdCard;

	private Boolean userIsVerified;
}
