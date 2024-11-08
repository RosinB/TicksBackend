package com.example.demo;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.entity.user.User;
import com.example.demo.repository.user.UserRepositoryJdbc;

@SpringBootTest
class MainAppTests {


	@Autowired
	UserRepositoryJdbc userRepositoryJdbc;
	
	@Test
	void add() {
		
		User user=new User( 1, "aaa", "aaa", "092522", "asa@gmail.com", "abc123", "123",new Timestamp(System.currentTimeMillis()), true);
		userRepositoryJdbc.addUser(user);		

	}
	
}
