package com.example.demo.model.dto.user;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDto {
	
	private Integer userId;

	private String userName;

	private String password;
	
	private String userPhone;

	private String userEmail;

	private String userIdCard;

	private Boolean userIsVerified;
}
