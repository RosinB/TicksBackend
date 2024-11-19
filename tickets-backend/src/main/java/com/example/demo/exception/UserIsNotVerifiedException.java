package com.example.demo.exception;

public class UserIsNotVerifiedException  extends RuntimeException{

	public UserIsNotVerifiedException(String message) {
		super(message);
	}
}
