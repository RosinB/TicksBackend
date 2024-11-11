package com.example.demo.model.dto.login;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {

	
	private Integer userId;
	@Size(min = 4, max = 10, message = "名字長度必須在 4 到 10 個字符之間")
	private String userName;

	private String password;
	
	
}
