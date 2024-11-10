package com.example.demo.model.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResultDto {

	private Boolean success;
	private String message;
	private LoginDto user;
	
}
