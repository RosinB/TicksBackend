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
	
    // 成功的建構子
    public LoginResultDto(LoginDto user) {
        this.success = true;
        this.message = "登入正確";
        this.user = user;
    }
    
    // 失敗的建構子
    public LoginResultDto(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
        this.user = null;
    }
	
}
